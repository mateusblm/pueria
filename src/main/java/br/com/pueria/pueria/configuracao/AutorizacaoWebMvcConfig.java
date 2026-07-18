package br.com.pueria.pueria.configuracao;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AutorizacaoWebMvcConfig implements WebMvcConfigurer {

    private final AutorizacaoCriancaInterceptor autorizacaoCriancaInterceptor;

    public AutorizacaoWebMvcConfig(AutorizacaoCriancaInterceptor autorizacaoCriancaInterceptor) {
        this.autorizacaoCriancaInterceptor = autorizacaoCriancaInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autorizacaoCriancaInterceptor)
                .addPathPatterns("/api/criancas/**");
    }
}
