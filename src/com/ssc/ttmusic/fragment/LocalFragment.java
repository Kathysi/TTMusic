package com.ssc.ttmusic.fragment;

import com.ssc.ttmusic.LocalSongActivity;
import com.ssc.ttmusic.MySongActivity;
import com.ssc.ttmusic.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class LocalFragment extends Fragment implements OnClickListener {

	private View view;
	private Button localSongsButton, favSongsButton, downButton, moreButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_local, null);
		initUi();
		return view;
	}

	private void initUi() {
		// TODO Auto-generated method stub
		localSongsButton = (Button) view.findViewById(R.id.local_bendiyinyue);
		favSongsButton = (Button) view.findViewById(R.id.local_shoucang);
		downButton = (Button) view.findViewById(R.id.local_xiazai);
		moreButton = (Button) view.findViewById(R.id.local_morerecom);
		localSongsButton.setOnClickListener(this);
		favSongsButton.setOnClickListener(this);
		downButton.setOnClickListener(this);
		moreButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.local_bendiyinyue:
			startActivity(new Intent(getActivity(), LocalSongActivity.class));
			break;

		case R.id.local_shoucang:

			startActivity(new Intent(getActivity(), MySongActivity.class));
			break;

		case R.id.local_xiazai:

			break;
		case R.id.local_morerecom:

			toast(getResources().getString(R.string.toastmsg_more));
			break;
		default:
			break;
		}
	}

	private void toast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}
}
