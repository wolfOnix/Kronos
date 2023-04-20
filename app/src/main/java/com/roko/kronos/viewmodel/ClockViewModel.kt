package com.roko.kronos.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.roko.kronos.exception.NoNetworkExc
import com.roko.kronos.processor.TimeProcessor
import com.roko.kronos.util.ClockState
import com.roko.kronos.util.Logger.log
import com.roko.kronos.util.asTimeString
import com.roko.kronos.util.asDifferenceString
import com.roko.kronos.util.asNullableStateFlowIn
import com.roko.kronos.util.asStateFlowIn
import com.roko.kronos.util.toHarmonisedNetworkTimeAndDeltaMillis
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlin.math.pow

@OptIn(ExperimentalCoroutinesApi::class)
class ClockViewModel(context: Context) : ViewModel() {

    /* The advantage of using a ViewModel is the fact that the variables survive activity lifecycles */

    /*
    * All data must be internally protected inside a view model. A simple type property can be accessed via a backing property with an overridden get():
    *
    * private var _count = 0
    * val count: Int
    *   get() = _count
    */

    /**
     * **Depends on [com.roko.kronos.util.timeFormatter]'s pattern**
     *
     * The precision drop represents the number of the least significant digit orders that can be omitted so that there would be no difference
     * in converting a Long millisecond number format to a digital time String.
     *
     * Example: if the number `12345L` (ms) would be displayed as `"00:00:12"` (0 h, 0 min, 12s), it means that the milliseconds (the last 3 digit
     * orders from the number) can be dropped as they are irrelevant/ not displayed: the precision drop is 3 in this case and the number `12000L`
     * can be used instead.
     */
    private val precisionDrop = 2 // preserve from the tenth of a second above
    /**
     * The refresh interval (ms) **must** be a divisor of `10 ^` [precisionDrop] so that both the device time and the network time as digital time
     * Strings would tick on the same time.
     *
     * If the [precisionDrop] is `2`, then the refresh interval must be a divisor of `10 ^ 2 = 100`, so any of `{1, 2, 4, ..., 25, 50, 100}`,
     * preferably a _quarter_ or at least _half_ of `10 ^` [precisionDrop] - `25` or `50`, but `100` is fine too for for this precision and from
     * performance aspects.
     */
    private val refreshInterval = 10f.pow(precisionDrop).toLong() // 100L for precisionDrop = 2L

    private val _clockState = MutableStateFlow(ClockState.PENDING)
    private val _deviceTimeMillis = MutableStateFlow(System.currentTimeMillis())
    private val _networkTimeMillis = MutableStateFlow<Long?>(null)
    private val _deltaMillis = MutableStateFlow<Long?>(null)

    val clockState: StateFlow<ClockState> = _clockState.asStateFlow()
    val deviceTime: StateFlow<String> = _deviceTimeMillis
        .map { millis: Long -> millis.asTimeString(context = context) }
        .asStateFlowIn(scope = viewModelScope, initialValue = System.currentTimeMillis().asTimeString(context = context))
    val networkTime: StateFlow<String?> = _networkTimeMillis
        .map { millis: Long? -> millis?.asTimeString(context = context) }
        .asNullableStateFlowIn(scope = viewModelScope)
    val timeDelta: StateFlow<Pair<Long?, String?>> = _deltaMillis
        .map { millis: Long? -> millis to millis?.div(1000L)?.asDifferenceString() } // todo - adjust truncation to the precision displayed on the UI
        .asStateFlowIn(scope = viewModelScope, initialValue = null to null)

    /*
    * The state could also be a data class that contains the device-, network- and other -times:
    *
    * data class TimeState(val deviceTime: String, ...)
    *
    * Then, the state can be updated partially (considering the state named _uiState of type UIState(score, isGuessedWordWrong, currentScrambledWord, currentWordCount, isGameOver, ...)):
    *
    *   fun checkUserGuess() {
    *      if (userGuess.equals(currentWord, ignoreCase = true)) {
    *          // User's guess is correct, increase the score
    *          // and update game state to prepare the game for next round
    *          val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
    *          _uiState.update { currentState -> // kotlinx.coroutines.flow.update
    *              currentState.copy(
    *                  isGuessedWordWrong = false,
    *                  currentScrambledWord = pickRandomWordAndShuffle(),
    *                  score = updatedScore
    *              )
    *          }
    *      } else {
    *          // User's guess is wrong, show an error
    *          _uiState.update { currentState ->
    *              currentState.copy(isGuessedWordWrong = true)
    *          }
    *      }
    *   }
    */

    private val getNetworkTime: Flow<Long?> = flow {
        val networkMillis: Long? = try {
            TimeProcessor.getNetworkTime()
        } catch (_: NoNetworkExc) {
            null
        }
        log("Emitting $networkMillis")
        emit(networkMillis)
    }

    private val updateTime: Flow<Pair<Long, Long?>> = flow {
        while (true) {
            emit((_deviceTimeMillis.value + refreshInterval) to _networkTimeMillis.value?.plus(refreshInterval))
            delay(refreshInterval)
        }
    }

    init {
        // find out networkTime and calculate delta
        getNetworkTime
            .onEach { networkMillis: Long? ->
                log("Received emission $networkMillis")
                if (networkMillis != null) {
                    val currentMillis = System.currentTimeMillis()
                    log("deviceMillis = $currentMillis, networkMillis = $networkMillis, deltaMillis = ${networkMillis - currentMillis}")
                    _deviceTimeMillis.update { currentMillis }
                    (currentMillis to networkMillis).toHarmonisedNetworkTimeAndDeltaMillis(precisionDrop = precisionDrop).let { (harmonisedNetworkTimeMillis, harmonisedDeltaMillis) ->
                        log("~networkMillis = $harmonisedNetworkTimeMillis, ~deltaMillis = $harmonisedDeltaMillis")
                        _networkTimeMillis.update { harmonisedNetworkTimeMillis }
                        _deltaMillis.update { harmonisedDeltaMillis }
                    }
                    _clockState.update { ClockState.KNOWN }
                } else {
                    _clockState.update { ClockState.NO_INTERNET }
                }
            }
            .launchIn(scope = viewModelScope)
        _clockState
            .flatMapLatest { updateTime }
            .onEach { (deviceMillis, networkMillis) -> _deviceTimeMillis.update { deviceMillis }; _networkTimeMillis.update { networkMillis } }
            .launchIn(viewModelScope)
        /*_clockState // causes crash when updating _clockState inside getNetworkTime.onEach above
            .flatMapLatest { clockState -> updateTime(clockState = clockState) }
            .launchIn(scope = viewModelScope)*/
    }

}

class ClockViewModelFactory(private val context: Context): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ClockViewModel(context = context) as T

}
