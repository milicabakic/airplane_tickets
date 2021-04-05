package app.security;

public final class SecurityConstants {

	public static final String SECRET = "mySecretKey";

	public static final long TOKEN_EXPIRATION_TIME = 86400000;
	public static final String TOKEN_PREFIX = "Basic ";
	public static final String HEADER_STRING = "Authorization";


	public static final String LOGIN_PATH = "/login";
	public static final String REGISTRATION_PATH = "/register";
	public static final String UPDATEMILES_PATH = "/updateMiles/{id}/{miles}";
    public static final String USERSRANK_PATH = "/whatRankAmI/{id}";
    public static final String CREDIT_CARD = "/creditCard/{id}";
    public static final String USERBYID ="/user/{id}";
    public static final String UPDATE_USER = "/updateUser/{id}";
}