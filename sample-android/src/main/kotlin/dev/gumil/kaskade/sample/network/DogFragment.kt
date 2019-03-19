package dev.gumil.kaskade.sample.network

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dev.gumil.kaskade.sample.R
import kotlinx.android.synthetic.main.fragment_dog.*

internal class DogFragment : Fragment(), Callback {

    private val dogViewModel by lazy {
        ViewModelProviders.of(this).get(DogViewModel::class.java)
    }

    private var currentState: DogState.OnLoaded? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.let { bundle ->
            bundle.getParcelable<DogState.OnLoaded>(ARG_STATE)?.let { state ->
                currentState = state
                dogViewModel.restore(state)
            }
        } ?: dogViewModel.restore()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_dog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dogViewModel.state.observe(this, Observer<DogState> {
            render(it)
        })

        buttonGetNewImage.setOnClickListener { dogViewModel.process(DogAction.Refresh) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentState?.let {
            outState.putParcelable(ARG_STATE, currentState)
        }
    }

    private fun render(state: DogState) = when (state) {
        is DogState.Loading -> {
            progressBar.visibility = View.VISIBLE
            imageView.visibility = View.GONE
        }
        is DogState.Error -> {
            Log.e(DogFragment::class.java.simpleName, "error", state.exception)
            Toast.makeText(context, "Error loading image", Toast.LENGTH_SHORT).show()
        }
        is DogState.OnLoaded -> {
            currentState = state
            Picasso.get().load(state.url).into(imageView, this)
        }
    }

    override fun onSuccess() {
        progressBar.visibility = View.GONE
        imageView.visibility = View.VISIBLE
    }

    override fun onError(e: java.lang.Exception) {
        dogViewModel.process(DogAction.OnError(e))
    }

    companion object {
        private const val ARG_STATE = "last_state"
    }
}
