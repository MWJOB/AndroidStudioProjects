//package com.dongguk.lastchatcalendar.utilities;
//
//import static com.dongguk.lastchatcalendar.utilities.Util.isStorageUrl;
//import static com.dongguk.lastchatcalendar.utilities.Util.showToast;
//import static com.dongguk.lastchatcalendar.utilities.Util.storageUrlToName;
//
//import android.app.Activity;
//
//import androidx.annotation.NonNull;
//
//import com.dongguk.lastchatcalendar.listeners.OnPostListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.util.ArrayList;
//
//public class FirebaseHelper {
//    private Activity activity;
//    private OnPostListener onPostListener;
//    private int successCount;
//
//    public FirebaseHelper(Activity activity){
//        this.activity = activity;
//    }
//
//    public void setOnPostListener(OnPostListener onPostListener){
//        this.onPostListener = onPostListener;
//    }
//
//    public void storageDelete(Writeinfo writeinfo){
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//
//        final String id = writeinfo.getId();
//        ArrayList<String> contentsList = writeinfo.getContents();
//        for (int i = 0; i < contentsList.size(); i++) {
//            String contents = contentsList.get(i);
//            if (isStorageUrl(contents)) {
//                successCount++;
//                StorageReference desertRef = storageRef.child("posts/" + id + "/" + storageUrlToName(contents));
//                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        successCount--;
//                        storeDelete(id);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        showToast(activity, "ERROR");
//                    }
//                });
//            }
//        }
//        storeDelete(id);
//
//    }
//
//    private void storeDelete(String id) {
//        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//        if (successCount == 0) {
//            firebaseFirestore.collection("posts").document(id)
//                    .delete()
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            showToast(activity, "게시글을 삭제하였습니다.");
//                            onPostListener.onDelete();
//                            //postUpdate();
//                        }
//                    })
//
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            showToast(activity, "게시글을 삭제하지 못했습니다.");
//                        }
//                    });
//
//            firebaseFirestore.collection("posts2").document(id)
//                    .delete()
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            showToast(activity, "게시글을 삭제하였습니다.");
//                            onPostListener.onDelete();
//                            //postUpdate();
//                        }
//                    })
//
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            showToast(activity, "게시글을 삭제하지 못했습니다.");
//                        }
//                    });
//
//            firebaseFirestore.collection("posts3").document(id)
//                    .delete()
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            showToast(activity, "게시글을 삭제하였습니다.");
//                            onPostListener.onDelete();
//                            //postUpdate();
//                        }
//                    })
//
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            showToast(activity, "게시글을 삭제하지 못했습니다.");
//                        }
//                    });
//        }
//    }
//}
