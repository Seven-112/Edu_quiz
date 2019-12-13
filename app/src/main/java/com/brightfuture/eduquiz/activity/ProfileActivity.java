package com.brightfuture.eduquiz.activity;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.brightfuture.eduquiz.Constant;
import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.helper.AndroidMultiPartEntity;
import com.brightfuture.eduquiz.helper.AppController;
import com.brightfuture.eduquiz.helper.CircleImageView;
import com.brightfuture.eduquiz.helper.Session;

import com.brightfuture.eduquiz.helper.Utils;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileActivity extends AppCompatActivity {


    public CircleImageView imgProfile;
    public ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public int reqReadPermission = 1;
    public int reqWritePermission = 2;
    public static int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static int SELECT_FILE = 110;
    Uri fileUri;
    public ProgressBar progressBar;
    private String filePath = null;
    File sourceFile;
    long totalSize = 0;
    public FloatingActionButton fabProfile;
    public TextView tvName, tvMobile, tvEmailId, tvLogout,tvSettings,tvPremium;
    public LinearLayout edtNameLayout, edtMobileLayout;
    public TextView tvPrivacy;
    public RelativeLayout mainLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mainLayout = findViewById(R.id.mainLayout);
        Utils.transparentStatusAndNavigation(ProfileActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle(getString(R.string.update_profile));
        edtNameLayout = findViewById(R.id.edtNameLayout);
        edtMobileLayout = findViewById(R.id.edtMobileLayout);

        tvName = findViewById(R.id.tvUserName);
        tvSettings=findViewById(R.id.tvSettings);
        tvMobile = findViewById(R.id.tvMobile);
        tvEmailId = findViewById(R.id.tvEmailId);
        tvLogout = findViewById(R.id.tvLogout);
        tvPrivacy = findViewById(R.id.tvPrivacy);
        fabProfile = findViewById(R.id.fabProfile);
        progressBar = findViewById(R.id.progressBar);
        tvPremium = findViewById(R.id.tvPremium);

        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setDefaultImageResId(R.drawable.ic_account);
        imgProfile.setImageUrl(Session.getUserData(Session.PROFILE, ProfileActivity.this), imageLoader);
        fabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectProfileImage();
            }
        });

        tvName.setText(Session.getUserData(Session.NAME, ProfileActivity.this));
        tvMobile.setText(Session.getUserData(Session.MOBILE, ProfileActivity.this));
        tvEmailId.setText(Session.getUserData(Session.EMAIL, ProfileActivity.this));


        edtNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialogName();
            }
        });

        edtMobileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialogMobile();
            }
        });
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignOutWarningDialog();
            }
        });
        tvPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, SubscriptionActivity.class);
                startActivity(intent);

            }
        });

        tvSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Settings();

            }
        });
        PrivacyPolicy();
    }
    public void Settings() {
        Utils.CheckVibrateOrSound(ProfileActivity.this);
        Intent playQuiz = new Intent(ProfileActivity.this, SettingActivity.class);
        startActivity(playQuiz);
        overridePendingTransition(R.anim.open_next, R.anim.close_next);

    }

    public void SignOutWarningDialog() {
        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(ProfileActivity.this);
        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.logout_warning));
        alertDialog.setCancelable(false);
        final android.app.AlertDialog alertDialog1 = alertDialog.create();

        // Setting OK Button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                Session.clearUserSession(ProfileActivity.this);
                LoginManager.getInstance().logOut();
                LoginActivity.mAuth.signOut();
                Intent intentLogin = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intentLogin);
                finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    public void UpdateProfile(final String name, final String mobile) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            System.out.println("--update res  " + obj.toString());
                            boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");
                            if (!error) {
                                Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();

                                Session.setUserData(Session.MOBILE, mobile, ProfileActivity.this);
                                Session.setUserData(Session.NAME, name, ProfileActivity.this);
                                tvName.setText(name);
                                tvMobile.setText(mobile);
                                FirebaseDatabase.getInstance().getReference(Constant.DB_USER)
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(Constant.USER_NAME).setValue(name);

                            } else {
                                Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.updateProfile, "1");
                params.put(Constant.email, Session.getUserData(Session.EMAIL, ProfileActivity.this));
                params.put(Constant.name, name);
                params.put(Constant.mobile, mobile);
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    public void SelectProfileImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, reqReadPermission);
            } else if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, reqWritePermission);
            } else {
                selectDialog();
            }
        } else {
            selectDialog();
        }
    }

    public void selectDialog() {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.from_library), getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.take_photo))) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                } else if (items[item].equals(getString(R.string.from_library))) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_FILE);
                } else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_FILE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setOutputCompressQuality(80)
                    .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setAspectRatio(1, 1)
                    .start(ProfileActivity.this);

        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(ProfileActivity.this, imageBitmap);
            System.out.println("***camera uri  " + tempUri);
            CropImage.activity(Uri.parse(String.valueOf(tempUri)))
                    .setGuidelines(CropImageView.Guidelines.OFF)
                    .setOutputCompressQuality(80)
                    .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setAspectRatio(1, 1)
                    .start(ProfileActivity.this);


        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                fileUri = result.getUri();
                new UploadFileToServer().execute();
            }
        }
    }


    public void BottomSheetDialogName() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ProfileActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.bootomsheet_layout, null);
        bottomSheetDialog.setContentView(view);
        Button save, cancel;
        final EditText edtName;
        save = view.findViewById(R.id.btnSave);
        cancel = view.findViewById(R.id.btnCancel);
        edtName = view.findViewById(R.id.edtName);
        edtName.setText(Session.getUserData(Session.NAME, ProfileActivity.this));

        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateProfile(edtName.getText().toString(), Session.getUserData(Session.MOBILE, ProfileActivity.this));
                bottomSheetDialog.cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
            }
        });
        bottomSheetDialog.show();
    }

    public void BottomSheetDialogMobile() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ProfileActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.bootomsheet_layout1, null);
        bottomSheetDialog.setContentView(view);
        Button save, cancel;
        final EditText edtMobile;
        save = view.findViewById(R.id.btnSave);
        cancel = view.findViewById(R.id.btnCancel);
        edtMobile = view.findViewById(R.id.edtMobile);
        edtMobile.setText(Session.getUserData(Session.MOBILE, ProfileActivity.this));

        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateProfile(Session.getUserData(Session.NAME, ProfileActivity.this), edtMobile.getText().toString());
                bottomSheetDialog.cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
            }
        });
        bottomSheetDialog.show();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }



    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constant.QUIZ_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                //publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                filePath = fileUri.getPath();
                sourceFile = new File(filePath);

                // Adding file data to http body
                entity.addPart(Constant.image, new FileBody(sourceFile));
                entity.addPart(Constant.accessKey, new StringBody(Constant.accessKeyValue));
                entity.addPart(Constant.userId, new StringBody(Session.getUserData(Session.USER_ID, ProfileActivity.this)));
                entity.addPart(Constant.upload_profile_image, new StringBody("1"));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            try {
                JSONObject jsonObject = new JSONObject(result);
                boolean error = jsonObject.getBoolean("error");
                if (!error) {
                    System.out.println("****update image " + jsonObject.toString());
                    Session.setUserData(Session.PROFILE, jsonObject.getString("file_path"), ProfileActivity.this);

                    imgProfile.setImageUrl(jsonObject.getString("file_path"), imageLoader);
                    FirebaseDatabase.getInstance().getReference(Constant.DB_USER)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(Constant.PROFILE_PIC).setValue(jsonObject.getString("file_path"));

                    Toast.makeText(ProfileActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //showAlert(result);
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(result);
        }

    }

    public void UploadImageWithVolly() {

    }

    ;

    public void PrivacyPolicy() {

        tvPrivacy.setClickable(true);
        tvPrivacy.setMovementMethod(LinkMovementMethod.getInstance());
        tvPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, PrivacyPolicy.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        String message = getString(R.string.privacy_terms1);
        String s2 = getString(R.string.term);
        String s1 = getString(R.string.privacy);
        final Spannable wordtoSpan = new SpannableString(message);

        wordtoSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, PrivacyPolicy.class);
                intent.putExtra("type", "privacy");
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#008fee"));
                ds.isUnderlineText();
            }
        }, message.indexOf(s1), message.indexOf(s1) + s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, PrivacyPolicy.class);
                intent.putExtra("type", "terms");
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#008fee"));
                ds.isUnderlineText();
            }
        }, message.indexOf(s2), message.indexOf(s2) + s2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPrivacy.setText(wordtoSpan);
    }
}
