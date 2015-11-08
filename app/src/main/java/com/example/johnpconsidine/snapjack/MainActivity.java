package com.example.johnpconsidine.snapjack;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;
    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;
    public static final int FILE_SIZE_LIMIT = 1024 * 1024 * 10;

    protected Uri mMediaUri;

    private DrawerLayout mDrawerLayout;
    private ListView mListView;
    private String[] mItems;
    private ActionBarDrawerToggle drawerListener;
    private Toolbar mToolbar;

    protected List<ParseObject> mMessages;

    protected DialogInterface.OnClickListener mDialogListner =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                            if (mMediaUri == null) {
                                Toast.makeText(MainActivity.this, R.string.external_storage_error, Toast.LENGTH_LONG).show();
                            }
                            else {
                                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);

                                startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                            }
                            break;
                        case 1:
                            Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                            if (mMediaUri == null) {
                                Toast.makeText(MainActivity.this, R.string.external_storage_error, Toast.LENGTH_LONG).show();
                            }
                            else {
                                videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                                videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                                startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
                            }
                            break;
                        case 2:
                            Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            choosePhotoIntent.setType("image/*");
                            startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
                            break;
                        case 3:
                             Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                             chooseVideoIntent.setType("video/*");                          ;
                             Toast.makeText(MainActivity.this, "The selected video must be less than 10 MB", Toast.LENGTH_LONG).show();
                            startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
                            break;
                    }
                }

                private Uri getOutputMediaFileUri(int mediaType) {
                    if (isExternalStorageAvailable()) {
                        String appName = MainActivity.this.getString(R.string.app_name);
                        File mediaStorageDir = new File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                appName);

                        if (! mediaStorageDir.exists() ){
                            if (! mediaStorageDir.mkdirs()) {
                                Log.e(TAG, "failed to create directory");
                                return null;
                            }
                        }

                        File mediaFile;
                        Date now = new Date();
                        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

                        String path = mediaStorageDir.getPath() + File.separator;
                        if (mediaType == MEDIA_TYPE_IMAGE) {
                            mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
                        }
                        else if (mediaType == MEDIA_TYPE_VIDEO) {
                            mediaFile = new File(path + "IMG" + timestamp + ".mp4");
                        }
                        else return null;
                        Log.i(TAG, "File: " + Uri.fromFile(mediaFile));
                        return Uri.fromFile(mediaFile);

                    }
                    else {
                        return null;
                    }


                }
                private boolean isExternalStorageAvailable() {
                    String state = Environment.getExternalStorageState();

                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        ParseUser currentUser = ParseUser.getCurrentUser();



        if (currentUser == null) {
            navigateToLogin();
        }
        else {
            Log.i(TAG, currentUser.getUsername());
        }
//SET NAV DRAWER:

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_drawer);
        setSupportActionBar(mToolbar);

        mItems = getResources().getStringArray(R.array.menu_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mListView = (ListView) findViewById(R.id.drawerList);
        mListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                mItems));
        mListView.setOnItemClickListener(this);

        drawerListener = new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar, R.string.open_nav_drawer, R.string.close_nav_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerClosed(drawerView);
                Toast.makeText(MainActivity.this, "Drawer opened", Toast.LENGTH_LONG).show();


            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }





        };

        //SET FRAGMENT
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        InboxFragment mInboxFragment = new InboxFragment();
        mFragmentTransaction.add(R.id.fragmentHolder, mInboxFragment);
        mFragmentTransaction.commit();

        mDrawerLayout.setDrawerListener(drawerListener);
        getSupportActionBar().setHomeButtonEnabled(true);  //Makes home button clickable
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            //See if its photo or video, PICK From gallary
            if (requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST) {
                if (data == null) // somethign went wrong
                    Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
                else {
                   mMediaUri = data.getData();
                }

                if (requestCode == PICK_VIDEO_REQUEST) {
                    Log.d(TAG, requestCode + "");

                    int fileSize = 0;
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this, R.string.error_opening_file, Toast.LENGTH_LONG).show();
                        return;
                    }
                        catch (IOException e) {
                        Toast.makeText(this, R.string.error_opening_file, Toast.LENGTH_LONG).show();
                            return;
                    }
                    finally {
                        try {
                            inputStream.close();
                        } catch (IOException e)   {}

                    }
                    if (fileSize >= FILE_SIZE_LIMIT) {
                        Toast.makeText(this, R.string.file_to_large, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
            else {
                //successful, add to gallary
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }
            Intent recipientIntent = new Intent(this, RecipientsActivity.class);
            recipientIntent.setData(mMediaUri);
            String filetype;
            if (requestCode == PICK_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST) {
                filetype = ParseConstants.TYPE_IMAGE;
            }
            else {
                filetype = ParseConstants.TYPE_VIDEO;
            }


            recipientIntent.putExtra(ParseConstants.KEY_FILE_TYPE, filetype);
            startActivity(recipientIntent);

        }
        else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, "Sorry, there was an error!", Toast.LENGTH_LONG).show();
        }
    }

    /** Swaps fragments in the main content view */



    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.camera:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices, mDialogListner);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 1) { // logout
            ParseUser.logOut();

            navigateToLogin();
        }
        else if (position == 0) {
            Intent intent = new Intent(this, EditFriendsActivity.class);
            startActivity(intent);

        }
        else if (position ==2) {
            Toast.makeText(MainActivity.this, R.string.banned, Toast.LENGTH_LONG).show();
            ParseUser.logOut();
            navigateToLogin();
        }


        selectItem(position);

    }
    public void selectItem (int position) {
        mListView.setItemChecked(position, true);
        setTitle(mItems[position]);
    }
    public void setTitle(String title) {
        mToolbar.setTitle(title);
    }


}






