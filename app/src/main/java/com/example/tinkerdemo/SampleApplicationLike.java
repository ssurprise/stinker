package com.example.tinkerdemo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.multidex.MultiDex;

import com.tencent.tinker.entry.DefaultApplicationLike;
import com.tencent.tinker.lib.listener.DefaultPatchListener;
import com.tencent.tinker.lib.listener.PatchListener;
import com.tencent.tinker.lib.patch.AbstractPatch;
import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.reporter.DefaultLoadReporter;
import com.tencent.tinker.lib.reporter.DefaultPatchReporter;
import com.tencent.tinker.lib.reporter.LoadReporter;
import com.tencent.tinker.lib.reporter.PatchReporter;
import com.tencent.tinker.lib.service.DefaultTinkerResultService;
import com.tencent.tinker.lib.tinker.TinkerInstaller;

public class SampleApplicationLike extends DefaultApplicationLike {

    public SampleApplicationLike(Application application,
                                 int tinkerFlags,
                                 boolean tinkerLoadVerifyFlag,
                                 long applicationStartElapsedTime,
                                 long applicationStartMillisTime,
                                 Intent tinkerResultIntent) {
        super(application,
                tinkerFlags,
                tinkerLoadVerifyFlag,
                applicationStartElapsedTime,
                applicationStartMillisTime,
                tinkerResultIntent);
    }

    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);

        // 注意：you must install multiDex whatever tinker is installed!
        // 在 MultiDex.install() 完成之前，不要通过反射或 JNI 执行 MultiDex.install() 或其他任何代码。
        // MultiDex 跟踪功能不会追踪这些调用，从而导致出现 ClassNotFoundException，或因 DEX 文件之间的类分区错误而导致验证错误。
        MultiDex.install(base);

        LoadReporter loadReporter = new DefaultLoadReporter(base);
        PatchReporter patchReporter = new DefaultPatchReporter(base);
        PatchListener patchListener = new DefaultPatchListener(base);
        AbstractPatch upgradePatchProcessor = new UpgradePatch();

        // 初始化tinker，必须！
        TinkerInstaller.install(this,
                loadReporter,//加载合成的包的报告类
                patchReporter,//打修复包过程中的报告类
                patchListener,//对修复包最开始的检查
                DefaultTinkerResultService.class, //patch包合成完成的后续操作服务
                upgradePatchProcessor);//生成一个新的patch合成包
    }

    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        getApplication().registerActivityLifecycleCallbacks(callback);
    }
}
