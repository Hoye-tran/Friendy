package com.example.social_network_friendy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends Activity {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference; // Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity_layout);

        // Khởi tạo Firebase Auth và Database
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users"); // Tạo tham chiếu đến nút "users"

        TextView textviewdangnhap = findViewById(R.id.textlogin);
        textviewdangnhap.setOnClickListener(view -> {
            // Chuyển sang màn hình đăng nhập
            Intent intentlogin = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intentlogin);
        });

        EditText emaildangki = findViewById(R.id.edtemail);
        EditText matkhau = findViewById(R.id.edtpassword);
        EditText xacnhanmatkhau = findViewById(R.id.edtconfirmpassword);
        EditText tennguoidung = findViewById(R.id.edtuser); // Thêm phần lấy tên người dùng
        Button chonnutdangki = findViewById(R.id.btncreateaccount);

        chonnutdangki.setOnClickListener(view -> {
            String email = emaildangki.getText().toString();
            String password = matkhau.getText().toString();
            String confirmPassword = xacnhanmatkhau.getText().toString();
            String username = tennguoidung.getText().toString(); // Lấy tên người dùng

            if (password.length() >= 8 && password.length() <= 20) {
                if (password.equals(confirmPassword)) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Đăng ký thành công
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                                    // Lưu thông tin người dùng vào Realtime Database
                                    if (user != null) {
                                        String userId = user.getUid();
                                        User newUser = new User(userId, username, email); // Tạo đối tượng User với userId

                                        databaseReference.child(userId).setValue(newUser)
                                                .addOnSuccessListener(aVoid -> {
                                                    // Chuyển sang màn hình đăng nhập
                                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(RegisterActivity.this, "Lưu thông tin người dùng thất bại", Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegisterActivity.this, "Mật khẩu phải từ 8-20 kí tự", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Tạo một lớp User để lưu thông tin người dùng vào Realtime Database
    public static class User {
        private String userId;
        private String username;
        private String email;

        public User() {
            // Constructor mặc định cho Realtime Database
        }

        public User(String userId, String username, String email) {
            this.userId = userId;
            this.username = username;
            this.email = email;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
