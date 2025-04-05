package guru.qa.niffler.config;

enum LocalConfig implements Config {
    instance;

    @Override
    public String frontUrl() {
        return "http://127.0.0.1:3000/";
    }

    @Override
    public String spendUrl() {
        return "http://127.0.0.1:8093/";
    }

    @Override
    public String ghUrl() {
        return "https://api.github.com/";
    }

    @Override
    public String userdataUrl() {
        return "http://127.0.0.1:8089/";
    }

    @Override
    public String authUrl() {
        return "http://127.0.0.1:9000/";
    }

    @Override
    public String gatewayUrl() {
        return "http://127.0.0.1:8090/";
    }
}
