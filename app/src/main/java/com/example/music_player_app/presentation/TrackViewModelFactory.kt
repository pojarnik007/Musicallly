import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.music_player_app.domain.usecase.AddTrackUseCase
import com.example.music_player_app.domain.usecase.DeleteTrackUseCase
import com.example.music_player_app.domain.usecase.GetTracksUseCase
import com.example.music_player_app.domain.usecase.DownloadAudioUseCase
import com.example.music_player_app.presentation.TrackViewModel

class TrackViewModelFactory(
    private val getTracksUseCase: GetTracksUseCase,
    private val addTrackUseCase: AddTrackUseCase,
    private val deleteTrackUseCase: DeleteTrackUseCase,
    private val downloadAudioUseCase: DownloadAudioUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrackViewModel(
                getTracksUseCase,
                addTrackUseCase,
                deleteTrackUseCase,
                downloadAudioUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}