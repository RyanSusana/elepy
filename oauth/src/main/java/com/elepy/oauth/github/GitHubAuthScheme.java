package com.elepy.oauth.github;

import com.elepy.oauth.AuthScheme;
import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.ServiceBuilder;

public class GitHubAuthScheme extends AuthScheme {
    private static final String GITHUB_ICON = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAADXklEQVRIS51VTYgcRRT+XnU3uOsfLttV3QOSCRs8uOAhR28LAZWcooi7JgERVDAx4CH/EDAhEYJ4UBOD4kWzWY0e9BKNwh70JAgeBEGyYsTdnurqyYoJCdpu1wvVmRl6emc3u9uXGep97/teffXqFWGVTym1GcCUKIoJFmIcwGgHnpG1v1qiWfK8Ga311ZVoaFAgiqImmE+D+RkAYrUiAFgQfUFCHGi1Wn/WscsEYil3sbXnIMS9dyHuD1t7k4R4pWXMdDXQJxBJuR/A6XURLwfv18a81V3uCZSVA590AkudX3+NYv8DcFwlnol2pml6wf0vBdxhUlH80rWFiN70guBEkedPM/AGgDEACQOmkyQBNAi4YomODQ0NffnvrVsOd6AsyNkVBOPuTEqBSKmLYH62W221gmazeU+e5/clSdKu7qbRaIwODw/fmJub+8+tx1LuZuDjHob5M51lk1RWzzxX65ZXtTHvr9GeEhaF4V4QvVvJsRBizAkcIeaTtZN/smXM5XUKPAWiS9UcZj5M8ejodyzEtoo9l9I03b4e8i42kvIbAE/0uIBvKZIycRZWvNurs+zMRgRUGL5GRO90cy0w7wRyAEFlB8+naTqzEYFYyp0MnK8I5E7A9XC13/doY85uRCAKwz0geq8u0GcRAx+mxry8IQGlPgLzi30W1Q/ZAovW2ma73b6xHpGRkZEHgiC4SswPVfIukwrDQ+7m1tp0pmXMrnJSru3zojCcBtFzNfhBKkeztb93LtqstXZMCLHJAj8IIY5prb9fRUgopSYE83EGHq+RF8L3N98ZFWE4A6JJC3xNRC+QtZ+CaMLFmOhvwXykZcy5KoEKw31EdBzAg4M2ydaeT9vt3aVAHMeb2A074H4GpoOiOLjkebMAHnHVM9GWNE3/qBKFYbjFI7qygoPXvaWl8YXFxfneuFZKTRGzG7Fuhjymtf5NSvmo7/tJfdB1SF2LDzojBvOkzrKLDtf34MRKvc7MbzOwQMxHhbU/QoihJMt+HlRpJCXX1h35Pp1lvbuw7MlUSk0S8wfOrm6yNmbw290v8A+YX9JZ9nmtI5fX1mg0Hi7y/BQJMQXAu4tAAeCCXxSH569dW6izDaysCyqFimJrmqZfDbIolnIH+f5PSZL8tdJ1uQ0Ll10mc3DWDgAAAABJRU5ErkJggg==";

    public GitHubAuthScheme(String clientId, String clientSecret) {
        super("GitHub",
                GITHUB_ICON, new ServiceBuilder(clientId)
                        .defaultScope("user user:email")
                        .apiSecret(clientSecret)
                        .build(GitHubApi.instance()),
                new GitHubEmailExtractor());
    }
}
