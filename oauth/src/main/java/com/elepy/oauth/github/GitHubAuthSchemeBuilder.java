package com.elepy.oauth.github;

public class GitHubAuthSchemeBuilder {
    private String clientId;
    private String clientSecret;

    public GitHubAuthSchemeBuilder clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public GitHubAuthSchemeBuilder clientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public GitHubAuthSchemeBuilder callback(String callback) {
        return this;
    }

    public GitHubAuthScheme createGitHubAuthScheme() {
        return new GitHubAuthScheme(clientId, clientSecret);
    }
}