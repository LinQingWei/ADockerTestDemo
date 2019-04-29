package com.adocker.test.components.job;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.widget.Toast;

import com.adocker.test.utils.LogUtil;

public class DemoJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        Toast.makeText(this, "DemoJobService onStartJob", Toast.LENGTH_SHORT).show();
        LogUtil.d("DemoJobService onStartJob");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Toast.makeText(this, "DemoJobService onStopJob", Toast.LENGTH_SHORT).show();
        LogUtil.d("DemoJobService onStopJob");
        return false;
    }
}
