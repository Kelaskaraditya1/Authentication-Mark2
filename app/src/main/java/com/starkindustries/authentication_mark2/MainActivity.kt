package com.starkindustries.authentication_mark2
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.starkindustries.authentication_mark2.databinding.ActivityMainBinding
class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    lateinit var auth:FirebaseAuth
    internal lateinit var mGoogleSigninClient:GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main)
        val gso =GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.defaultId))
            .requestEmail()
            .build()
        mGoogleSigninClient=GoogleSignIn.getClient(this,gso)
        binding.signinButton.setOnClickListener()
        {
            val signinintent = mGoogleSigninClient.signInIntent
            startActivityForResult(signinintent, RC_SIGN_IN)

        }
        binding.logout.setOnClickListener()
        {
            auth.signOut()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    override fun onStart() {
        super.onStart()
        val currentUser = auth!!.currentUser
        if(currentUser!=null)
            Toast.makeText(this@MainActivity, "Currently Logged In "+currentUser.email, Toast.LENGTH_SHORT).show()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== RC_SIGN_IN)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try
            {
                val account = task.getResult(ApiException::class.java)
                Toast.makeText(this@MainActivity, "Signin Sucessfully Done ", Toast.LENGTH_SHORT).show()
                val credentials = GoogleAuthProvider.getCredential(account.idToken,null)
                auth.signInWithCredential(credentials).addOnCompleteListener(this)
                {task->
                    if(task.isSuccessful)
                    {
                        val user = auth.currentUser
                        Toast.makeText(applicationContext, "Firebase Authentication Successfull", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(applicationContext, "Failed Firebas Auth", Toast.LENGTH_SHORT).show()
                    }

                }
            }
            catch(e:Exception)
            {
                Toast.makeText(this@MainActivity, "signin failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object
    {
        val TAG = "GoogleActivity"
        val RC_SIGN_IN=9001
    }
}