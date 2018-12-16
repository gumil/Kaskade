package io.gumil.kaskade.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_main.*

internal class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonStartTodo.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.todoFragment))

        buttonStartNetwork.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.dogFragment))
    }
}