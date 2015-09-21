package de.tum.in.tumcampus.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import de.tum.in.tumcampus.R;
import de.tum.in.tumcampus.activities.generic.ActivityForDownloadingExternal;
import de.tum.in.tumcampus.adapters.CafeteriaDetailsSectionsPagerAdapter;
import de.tum.in.tumcampus.auxiliary.Const;
import de.tum.in.tumcampus.auxiliary.NetUtils;
import de.tum.in.tumcampus.auxiliary.Utils;
import de.tum.in.tumcampus.models.Cafeteria;
import de.tum.in.tumcampus.models.managers.LocationManager;

import static de.tum.in.tumcampus.fragments.CafeteriaDetailsSectionFragment.menuToSpan;

/**
 * Lists all dishes at selected cafeteria
 *
 * OPTIONAL: Const.CAFETERIA_ID set in incoming bundle (cafeteria to show)
 */
public class CafeteriaActivity extends ActivityForDownloadingExternal implements AdapterView.OnItemSelectedListener {

    private ViewPager mViewPager;
    private int mCafeteriaId = -1;
    private CafeteriaDetailsSectionsPagerAdapter mSectionsPagerAdapter;
    private List<Cafeteria> mCafeterias;

    public CafeteriaActivity() {
        super(Const.CAFETERIAS, R.layout.activity_cafeteria);
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get id from intent if specified
        final Intent intent = getIntent();
        if(intent!=null && intent.getExtras()!=null && intent.getExtras().containsKey(Const.CAFETERIA_ID))
    		mCafeteriaId = intent.getExtras().getInt(Const.CAFETERIA_ID);
        mViewPager = (ViewPager) findViewById(R.id.pager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        // Add info icon to show ingredients
		getMenuInflater().inflate(R.menu.menu_section_fragment_cafeteria_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==R.id.action_ingredients) {
			// Build a alert dialog containing the mapping of ingredients to the numbers
			new AlertDialog.Builder(this).setTitle(R.string.action_ingredients)
			    .setMessage(menuToSpan(this, getResources().getString(R.string.cafeteria_ingredients)))
                .setPositiveButton(android.R.string.ok, null).create().show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    /**
     * Setup action bar navigation (to switch between cafeterias)
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Get all available cafeterias from database
        mCafeterias = new LocationManager(this).getCafeterias();

        // If something went wrong or no cafeterias found
        if (mCafeterias.size() == 0) {
            if(!NetUtils.isConnected(this)) {
                showNoInternetLayout();
            } else {
                showErrorLayout();
            }
            return;
        }

        int selIndex = -1;
        for(int i=0;i<mCafeterias.size();i++) {
            Cafeteria c = mCafeterias.get(i);
            if(mCafeteriaId==-1 || mCafeteriaId == c.id) {
                mCafeteriaId = c.id;
                selIndex = i;
                break;
            }
        }

        // Adapter for drop-down navigation
        ArrayAdapter<Cafeteria> adapterCafeterias = new ArrayAdapter<Cafeteria>(this, R.layout.simple_spinner_item_actionbar, android.R.id.text1, mCafeterias ) {
            final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = inflater.inflate(R.layout.simple_spinner_dropdown_item_actionbar, parent, false);
                Cafeteria c = getItem(position);

                // Set name
                TextView name = (TextView) v.findViewById(android.R.id.text1);
                name.setText(c.name);

                // Set address
                TextView address = (TextView) v.findViewById(android.R.id.text2);
                address.setText(c.address);

                // Set distance
                TextView dist = (TextView) v.findViewById(R.id.distance);
                dist.setText(Utils.formatDist(c.distance));

                return v;
            }
        };

        Spinner spinner = (Spinner) findViewById(R.id.spinnerToolbar);
        spinner.setAdapter(adapterCafeterias);
        spinner.setOnItemSelectedListener(this);
        // Select item
        if(selIndex>-1)
            spinner.setSelection(selIndex);
    }

    /**
     * Switch cafeteria if a new cafeteria has been selected
     * @param parent the parent view
     * @param pos index of the new selection
     * @param id id of the selected item
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        mCafeteriaId = mCafeterias.get(pos).id;

        // Create the adapter that will return a fragment for each of the primary sections of the app.
        if (mSectionsPagerAdapter == null) {
            mSectionsPagerAdapter = new CafeteriaDetailsSectionsPagerAdapter(getSupportFragmentManager());
            mSectionsPagerAdapter.setCafeteriaId(this, mCafeteriaId);
            mViewPager.setAdapter(mSectionsPagerAdapter);
        } else {
            mViewPager.setAdapter(null); //unset the adapter for updating
            mSectionsPagerAdapter.setCafeteriaId(this, mCafeteriaId);
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Don't change anything
    }
}
