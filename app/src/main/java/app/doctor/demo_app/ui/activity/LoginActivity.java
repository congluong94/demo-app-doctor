package app.doctor.demo_app.ui.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import javax.inject.Inject;

import app.doctor.demo_app.R;
import app.doctor.demo_app.base.BaseActivity;
import app.doctor.demo_app.data.remote.Status;
import app.doctor.demo_app.databinding.LoginActivityBinding;
import app.doctor.demo_app.utils.Constants;
import app.doctor.demo_app.utils.Utils;
import app.doctor.demo_app.viewmodel.LoginViewModel;

/**
 * Created by luonglc on 7/6/2020
 * E: lecongluong94@gmail.com
 * C: ANTS Programmatic Company
 * A: HCMC, VN
 */
public class LoginActivity extends BaseActivity<LoginActivityBinding> implements View.OnClickListener {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private LoginViewModel viewModel;
    private boolean isGoMain = true;

    @Override
    public int getLayoutRes() {
        return R.layout.login_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel.class);

        dataBinding.btnLogin.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        isGoMain = true;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == dataBinding.btnLogin.getId()) {
            if (validate(dataBinding.editUserName.getText().toString().trim(), dataBinding.editPassword.getText().toString().trim())) {
                showLoadingDialog();
                viewModel.setUserInfo(dataBinding.editUserName.getText().toString().trim(), dataBinding.editPassword.getText().toString().trim());
                viewModel.login().observe(this, resource -> {
                    if (resource.status == Status.SUCCESS && resource.data != null && !TextUtils.isEmpty(resource.data.getMemberId()) && isGoMain) {
                        Utils.savePreference(Constants.PREF_MEMBER_IDX, resource.data.getMemberIdx());
                        gotoMainActivity();
                    } else if (resource.status == Status.ERROR) {
                        hideLoadingDialog();
                        showErrorDialog(resource.getCodeMsg(), false);
                    }
                });
            }
        }
    }

    /**
     * Check username and password must not be blank
     *
     * @param username username of the user
     * @param password password
     */
    private boolean validate(String username, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            showErrorDialog(getString(R.string.login_not_validate), false);
            return false;
        }
        return TextUtils.isEmpty(username) || android.util.Patterns.EMAIL_ADDRESS.matcher(username.trim()).matches();
    }

    private void gotoMainActivity() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            isGoMain = false;
            finish();
        }, 500);
    }


}
