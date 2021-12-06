/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot.translation;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.tmt.v20180321.TmtClient;
import com.tencentcloudapi.tmt.v20180321.models.TextTranslateRequest;

class TencentTranslator implements Translator {
    private final TmtClient client;
    private final Long projectId;

    TencentTranslator(String secretId, String secretKey, String region, Long projectId) {
        Credential cred = new Credential(secretId, secretKey);
        client = new TmtClient(cred, region);
        this.projectId = projectId;
    }

    @Override
    public String translate(String source) throws TranslateException {
        var req = new TextTranslateRequest();
        req.setSourceText(source);
        req.setSource("en");
        req.setTarget("zh");
        req.setProjectId(projectId);
        try {
            return client.TextTranslate(req).getTargetText();
        } catch (TencentCloudSDKException e) {
            throw new TranslateException(e);
        }
    }
}
