package ma.ac.uir.projets8.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Getter
@Setter
@Builder
public class Mail {

    @Getter
    @Setter
    public static class HtmlTemplate {
        private String template;
        private Map<String, Object> props;

        public HtmlTemplate(String template, Map<String, Object> props) {
            this.template = template;
            this.props = props;
        }
    }

    private String to;

    private String subject;

    private HtmlTemplate htmlTemplate;
}
