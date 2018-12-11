package io.gumil.kaskade.sample.network

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.gumil.kaskade.sample.R
import kotlinx.android.synthetic.main.fragment_dog.*
import java.lang.Exception

internal class DogFragment : Fragment() {

    private val dogKaskade = DogKaskade(ApiFactory.create())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dogKaskade.state.subscribe { render(it) }

        dogKaskade.process(DogAction.Refresh)

        buttonGetNewImage.setOnClickListener { dogKaskade.process(DogAction.Refresh) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dogKaskade.unsubscribe()
    }

    private fun render(state: DogState) {
        return when (state) {
            is DogState.Loading -> {
                progressBar.visibility = View.VISIBLE
                imageView.visibility = View.GONE
            }
            is DogState.OnLoaded -> {
                Picasso.get().load(state.url).into(imageView, object : Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                        imageView.visibility = View.VISIBLE
                    }

                    override fun onError(e: Exception?) {
                        Toast.makeText(context, "Error loading image", Toast.LENGTH_SHORT).show()
                        Log.e("tantrums", "error", e)
                    }

                })
            }
        }
    }
}