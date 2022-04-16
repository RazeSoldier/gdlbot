/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot.translation;

import com.google.inject.Inject;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.tmt.v20180321.TmtClient;
import com.tencentcloudapi.tmt.v20180321.models.TextTranslateRequest;
import org.jetbrains.annotations.NotNull;
import razesoldier.gdlbot.Config;

class TencentTranslator implements Translator {
    private final TmtClient client;
    private final Long projectId;

    @Inject
    TencentTranslator(@NotNull @TranslatorModule.TencentCredential Config.TencentCredential credential) {
        Credential cred = new Credential(credential.secretId(), credential.secretKey());
        client = new TmtClient(cred, credential.region());
        this.projectId = credential.projectId();
    }

    @Override
    public String translate(String source) throws TranslateException {
        var req = new TextTranslateRequest();
        req.setSourceText(source);
        req.setSource("en");
        req.setTarget("zh");
        req.setProjectId(projectId);
        req.setUntranslatedText("CCP"); // 不要翻译CCP
        try {
            return client.TextTranslate(req).getTargetText();
        } catch (TencentCloudSDKException e) {
            throw new TranslateException(e);
        }
    }
}
