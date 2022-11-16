package com.dongguk.lastchatcalendar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.dongguk.lastchatcalendar.databinding.ActivitySignUpBinding;
import com.dongguk.lastchatcalendar.ChatActivity.utilities.Constants;
import com.dongguk.lastchatcalendar.ChatActivity.utilities.PreferenceManger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private PreferenceManger preferenceManger;
    private String encodedImage;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth;
    private DatabaseReference reference;
    private String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());

        fAuth = FirebaseAuth.getInstance();
        setContentView(binding.getRoot());
        preferenceManger = new PreferenceManger(getApplicationContext());
        setListeners();

    }

    private void setListeners(){
        binding.textSignIn.setOnClickListener(v -> onBackPressed());
        binding.buttonSignUp.setOnClickListener(v -> {
            if(isValidSignUpDetails()){
                signUp();
            }
        });
        binding.layoutImage.setOnClickListener(v ->{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    //회원가입 로직
    private void signUp() {
        String email = binding.inputEmail.getText().toString();
        String password = binding.inputPassword.getText().toString();



        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                loading(true);


                // 인증 메일 보내기 로직
//                VerifiedUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Toast.makeText(SignUpActivity.this, "인증 메일이 전송되었습니다.", Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "전송 실패: Email not sent " + e.getMessage());
//                    }
//                });



                // 회원가입 로직
                HashMap<String, Object> user = new HashMap<>();
                user.put(Constants.KEY_NAME, binding.inputName.getText().toString());
                user.put(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
                user.put(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());
                user.put(Constants.KEY_UNIVERSITY, binding.inputUniversity.getText().toString());
                user.put(Constants.KEY_IMAGE, encodedImage);
                FirebaseUser userId = fAuth.getCurrentUser();
                assert userId != null;
                String userid = userId.getUid();
                //파이어데이터베이스 추가
                reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                reference.setValue(user);

                //파이어스토어 추가
                database.collection(Constants.KEY_COLLECTION_USERS)
                        .add(user)
                        .addOnSuccessListener(documentReference -> {
                            loading(false);
                            preferenceManger.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                            preferenceManger.putString(Constants.KEY_USER_ID, documentReference.getId());
                            preferenceManger.putString(Constants.KEY_NAME, binding.inputName.getText().toString());
                            preferenceManger.putString(Constants.KEY_UNIVERSITY, binding.inputUniversity.getText().toString());
                            preferenceManger.putString(Constants.KEY_IMAGE, encodedImage);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//새로운 Activity를 수행하고 현재 Activity를 스택에서 제거하기
                            startActivity(intent);
                        })
                        .addOnFailureListener(exception -> {
                            loading(false);
                            showToast(exception.getMessage());
                        });
            }
            else{
                showToast("이미 존재하는 이메일입니다.");
            }
        }));
    }
    //1이미지처리
    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    //2 //주석1
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage= encodeImage(bitmap);

                        }catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private Boolean isValidSignUpDetails() {
        if(encodedImage == null){
            showToast("Select profile image");
            return false;
        }else if(binding.inputName.getText().toString().trim().isEmpty()){
            showToast("Enter name");
            return false;
        }else if(binding.inputEmail.getText().toString().trim().isEmpty()){
            showToast("Enter email");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Enter valid email");
            return false;
        }else if(binding.inputUniversity.getText().toString().trim().isEmpty()){
            showToast("Enter email");
            return false;
        }else if(binding.inputPassword.getText().toString().trim().isEmpty()){
            showToast("Enter password");
            return false;
        }else if(binding.inputConfirmPassword.getText().toString().trim().isEmpty()){
            showToast("Confirm your password");
            return false;
        }else if(!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString())){
            showToast("Password & confirm password must be same");
            return false;
        }else {
            return true;
        }
    }

    //인증시 동작 아직 사용하지 않음.
//    private boolean EmailVerified(){
//        VerifiedUser.isEmailVerified();
//        return false;
//    }
    //로딩창에서 프로그세스바와 회원가입버튼 보이기 설정
    private void loading(Boolean isLoading){
        if(isLoading) {
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignUp.setVisibility(View.VISIBLE);
        }
    }
}