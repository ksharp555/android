package com.example.retrievevideo;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class PersonalInfoFragment extends Fragment{
	
	Button signoutBtn;
	SharedPreferences sharedpreferences;
	TextView textBtn;
	private static final String MyPREFERENCES = "MyPrefs";
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		 View rootView = inflater.inflate(R.layout.personal_info, container, false);
		 signoutBtn = (Button)rootView.findViewById(R.id.signoutBtn);
		 textBtn = (TextView)rootView.findViewById(R.id.manageevent);
		 signoutBtn.setOnClickListener(new View.OnClickListener() {
			 @Override
			 public void onClick(View view) {
				 getActivity().finish();
			     Intent intent = new Intent(getActivity(), RegistrationActivity.class);
				 SharedPreferences sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
			     sharedpreferences.edit().clear().commit();
			     startActivity(intent);
             }
		 });
		 textBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), personalEventActivity.class);
				startActivity(intent);
			}
		});
		 return rootView;
	 }
}
