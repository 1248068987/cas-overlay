//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apereo.cas.authentication.handler.support;

import java.security.GeneralSecurityException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.PrincipalNameTransformer;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.support.password.PasswordPolicyConfiguration;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class AbstractUsernamePasswordAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractUsernamePasswordAuthenticationHandler.class);
    private PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();
    private PrincipalNameTransformer principalNameTransformer = (formUserId) -> {
        return formUserId;
    };
    private PasswordPolicyConfiguration passwordPolicyConfiguration;

    public AbstractUsernamePasswordAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    protected HandlerResult doAuthentication(Credential credential) throws GeneralSecurityException, PreventedException {

        UsernamePasswordCredential originalUserPass = (UsernamePasswordCredential)credential;
        UsernamePasswordCredential userPass = new UsernamePasswordCredential(originalUserPass.getUsername(), originalUserPass.getPassword());
        if (StringUtils.isBlank(userPass.getUsername())) {
            throw new AccountNotFoundException("Username is null.");
        } else {
            LOGGER.debug("Transforming credential username via [{}]", this.principalNameTransformer.getClass().getName());
            String transformedUsername = this.principalNameTransformer.transform(userPass.getUsername());
            if (StringUtils.isBlank(transformedUsername)) {
                throw new AccountNotFoundException("Transformed username is null.");
            } else if (StringUtils.isBlank(userPass.getPassword())) {
                throw new FailedLoginException("Password is null.");
            } else {
                LOGGER.debug("Attempting to encode credential password via [{}] for [{}]", this.passwordEncoder.getClass().getName(), transformedUsername);
                String transformedPsw = this.passwordEncoder.encode(userPass.getPassword());
                if (StringUtils.isBlank(transformedPsw)) {
                    throw new AccountNotFoundException("Encoded password is null.");
                } else {
                    userPass.setUsername(transformedUsername);
                    userPass.setPassword(transformedPsw);
                    LOGGER.debug("Attempting authentication internally for transformed credential [{}]", userPass);
                    return this.authenticateUsernamePasswordInternal(userPass, originalUserPass.getPassword());
                }
            }
        }
    }

    protected abstract HandlerResult authenticateUsernamePasswordInternal(UsernamePasswordCredential var1, String var2) throws GeneralSecurityException, PreventedException;

    protected PasswordPolicyConfiguration getPasswordPolicyConfiguration() {
        return this.passwordPolicyConfiguration;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setPrincipalNameTransformer(PrincipalNameTransformer principalNameTransformer) {
        this.principalNameTransformer = principalNameTransformer;
    }

    public void setPasswordPolicyConfiguration(PasswordPolicyConfiguration passwordPolicyConfiguration) {
        this.passwordPolicyConfiguration = passwordPolicyConfiguration;
    }

    public boolean supports(Credential credential) {
        if (!UsernamePasswordCredential.class.isInstance(credential)) {
            LOGGER.debug("Credential is not one of username/password and is not accepted by handler [{}]", this.getName());
            return false;
        } else if (this.credentialSelectionPredicate == null) {
            LOGGER.debug("No credential selection criteria is defined for handler [{}]. Credential is accepted for further processing", this.getName());
            return true;
        } else {
            LOGGER.debug("Examining credential [{}] eligibility for authentication handler [{}]", credential, this.getName());
            boolean result = this.credentialSelectionPredicate.test(credential);
            LOGGER.debug("Credential [{}] eligibility is [{}] for authentication handler [{}]", new Object[]{credential, this.getName(), BooleanUtils.toStringTrueFalse(result)});
            return result;
        }
    }

    protected boolean matches(CharSequence charSequence, String password) {
        return this.passwordEncoder.matches(charSequence, password);
    }
}
