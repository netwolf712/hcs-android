/*
 * Copyright © 2019 Zhenjie Yan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hcs.android.server;

import android.content.Context;

import com.hcs.android.server.util.AccessObjectUtil;
import com.yanzhenjie.andserver.annotation.Config;
import com.yanzhenjie.andserver.framework.config.Multipart;
import com.yanzhenjie.andserver.framework.config.WebConfig;
import com.yanzhenjie.andserver.framework.website.AssetsWebsite;

/**
 * Created by Zhenjie Yan on 2019-06-30.
 */
@Config
public class AppConfig implements WebConfig {

    @Override
    public void onConfig(Context context, Delegate delegate) {
        //业务相关页面
        delegate.addWebsite(new AssetsWebsite(context, "/web"));
        //运维相关页面
        //delegate.addWebsite(new AssetsWebsite(context, "/business"));
        delegate.setMultipart(Multipart.newBuilder()
            .allFileMaxSize(100 * com.hcs.android.server.config.Config.MAX_SIZE_IN_MEMORY) // 1G
            .fileMaxSize( 100 * com.hcs.android.server.config.Config.MAX_SIZE_IN_MEMORY) // 1G
            .maxInMemorySize(com.hcs.android.server.config.Config.MAX_SIZE_IN_MEMORY) // 10M
            .uploadTempDir(AccessObjectUtil.getTempFileDir())
            .build());
    }
}