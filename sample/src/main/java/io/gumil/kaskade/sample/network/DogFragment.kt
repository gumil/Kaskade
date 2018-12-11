package io.gumil.kaskade.sample.network

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.gumil.kaskade.sample.R
import kotlinx.android.synthetic.main.fragment_dog.*

internal class DogFragment : Fragment(), Callback {

    private val dogViewModel by lazy {
        ViewModelProviders.of(this).get(DogViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dogViewModel.state.subscribe { render(it) }

        buttonGetNewImage.setOnClickListener { dogViewModel.process(DogAction.Refresh) }
    }

    private fun render(state: DogState) {
        return when (state) {
            is DogState.Loading -> {
                progressBar.visibility = View.VISIBLE
                imageView.visibility = View.GONE
            }
            is DogState.Error -> {
                Log.e(DogFragment::class.java.simpleName, "error", state.exception)
                Toast.makeText(context, "Error loading image", Toast.LENGTH_SHORT).show()
            }
            is DogState.OnLoaded -> {
                Picasso.get().load(state.url).into(imageView, this)
            }
            is DogState.Success -> {
                progressBar.visibility = View.GONE
                imageView.visibility = View.VISIBLE
            }
        }
    }

    override fun onSuccess() {
        dogViewModel.process(DogAction.OnSuccess)
    }

    override fun onError(e: java.lang.Exception) {
        dogViewModel.process(DogAction.OnError(e))
    }
}