package methods;

import android.app.Activity;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class StorageMethod {
    private Activity activity;
    private Uri selectedImageUri;
    public StorageMethod(Uri uri, Activity activity){
        this.selectedImageUri = uri;
        this.activity = activity;
    }
    public void uploadImage(){
        if(selectedImageUri !=null){
            // Lấy tham chiếu tới Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            // Tạo tên tệp tin duy nhất cho ảnh
            String fileName = "image_" + System.currentTimeMillis() + ".jpg";

            // Tạo tham chiếu tới thư mục lưu trữ trong Firebase Storage
            StorageReference imageRef = storageRef.child("images/" + fileName);

            // Tải ảnh lên Firebase Storage
            UploadTask uploadTask = imageRef.putFile(selectedImageUri);

            // Lắng nghe sự kiện hoàn thành việc tải lên
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        // Lấy URL của ảnh đã tải lên từ Firebase Storage
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                new FireStoreMethod().addMessage("", FirebaseAuth.getInstance().getCurrentUser().getUid(),downloadUri.toString(),"","", new Date());
                            }
                        });
                    } else {
                        // Xử lý lỗi nếu tải lên không thành công
                        Toast.makeText(activity, "Upload failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
