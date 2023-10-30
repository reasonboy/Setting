package com.jzzh.setting.setupwizard;

import android.content.Intent;
import android.os.Bundle;

import com.inno.spacesetupwizardlib.SetupWizardActivity;

/*
    Need to add libs/SpaceSetupWizardLib.aar in build.gradle for SetupWizardActivity

    We want to make this WifiSetupWizardActivity and WifiSetupWizardFragment in settings app
 */
public class WifiSetupWizardActivity extends SetupWizardActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addFragment(WifiSetupWizardFragment.newInstance(), "wifi");
    }

    @Override
    public void onComplete() {
        super.onComplete();
    }

    @Override
    protected void onSetupWizardResult(String tag, Intent intent) {
        super.onSetupWizardResult(tag, intent);
    }

    @Override
    protected String onSkipped(String tag) {
        return null;
    }

}
