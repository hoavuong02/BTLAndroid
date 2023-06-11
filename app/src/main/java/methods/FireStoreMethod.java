package methods;

import com.example.btl.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import models.Message;

public class FireStoreMethod {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public interface DataCallback {
        void onDataLoaded(User user);
        void onError(Exception e);
    }

    //Sử dụng callback để xử lý bất đồng bộ (asynchronous )
    public void getUserByUid(String uid, DataCallback callback) {
        CollectionReference usersRef = firestore.collection("users");
        usersRef.whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // User with the specified UID found
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            String username = documentSnapshot.getString("username");
                            String email = documentSnapshot.getString("email");
                            String photoUrl = documentSnapshot.getString("photoUrl");
                            String token = documentSnapshot.getString("token");

                            // Create the User object
                            User user = new User(uid, username, email, photoUrl, token);

                            // Pass the data to the callback
                            callback.onDataLoaded(user);
                        } else {
                            // User with the specified UID not found
                            // Pass an appropriate error message to the callback
                            callback.onError(new Exception("User not found"));
                        }
                    } else {
                        // An error occurred while fetching the data
                        Exception exception = task.getException();
                        // Pass the exception to the callback
                        callback.onError(exception);
                    }
                });
    }
    public void addMessage(String mText, String mSender, String mPhotoURL, String mFileName, String mFileURL, Date mDate) {

        Message message = new Message(mText, mSender, "","","", mDate);

        // Get the collection reference
        CollectionReference collectionRef = firestore.collection("messages");

        // Add the document to the collection
        collectionRef.add(message)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            DocumentReference documentRef = task.getResult();
                            if (documentRef != null) {
                                System.out.println("Document added with ID: " + documentRef.getId());
                            }
                        } else {
                            Exception e = task.getException();
                            if (e != null) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    public void deleteMessage(String messageId) {
        // Get the document reference using the message ID
        DocumentReference documentRef = firestore.collection("messages").document(messageId);

        // Delete the document
        documentRef.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            System.out.println("Message deleted successfully");
                        } else {
                            Exception e = task.getException();
                            if (e != null) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }
}
