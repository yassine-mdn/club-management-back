package ma.ac.uir.projets8.model.enums;

public enum PropertyKey {

	APP_SERVER_PORT("server.port"),
	CLIENT_ID("azure.activedirectory.client-id"),

	TENANT_ID("azure.activedirectory.tenant-id"),
	CLIENT_SECRET("azure.activedirectory.client-secret"),
	SCOPES("azure.activedirectory.scopes"),
	POST_LOGOUT_REDIRECT_URI("azure.activedirectory.post-logout-redirect-uri");
	
	private String key;
	
	private PropertyKey(String key) {
		this.key = key;
	}

	public String key() {
		return key;
	}
	
}
