package inc.can_a.nmfarm.activity;


import android.app.FragmentTransaction;
import android.os.Bundle;


import android.support.v7.app.AppCompatActivity;


import inc.can_a.nmfarm.R;
import inc.can_a.nmfarm.fragment.SearchFragment;


public class MainSearchActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_search);

		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		/*
		 * When this container fragment is created, we fill it with our first
		 * "real" fragment
		 */
		transaction.replace(R.id.root_frame, new SearchFragment());
		//transaction.replace(R.id.root_frame, new SearchFragment());

		transaction.commit();



	}
}
