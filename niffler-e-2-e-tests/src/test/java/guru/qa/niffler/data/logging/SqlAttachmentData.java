package guru.qa.niffler.data.logging;

import io.qameta.allure.attachment.AttachmentData;
import lombok.Getter;

@Getter
public class SqlAttachmentData implements AttachmentData {

    public SqlAttachmentData(String name, String sql) {
        this.name = name;
        this.sql = sql;
    }

    private final String name;
    private final String sql;

}
