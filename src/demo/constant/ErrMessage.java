package demo.constant;

public interface ErrMessage {

    @FunctionalInterface
    public interface MsgBaseWithParams{
        String get(Object... args);
    }

    @FunctionalInterface
    public interface MsgBase{
        String get();
    }

    public static final MsgBaseWithParams ID_NOT_FOUND = "id %s not found"::formatted;
    public static final MsgBase BODY_NOT_FOUND = () -> "body request not found";
    public static final MsgBase NAME_IS_REQUIRED = () -> "name is required";
    public static final MsgBase NAME_MUST_BE_LESS_THAN_50_CARACT = () -> "name must be less than 50 caract";
    public static final MsgBase ID_IS_REQUIRED = () -> "id is required";
    public static final MsgBase NUMPAGE_MUST_BE_MORE_THAN_OR_EQUALS_ZERO = () -> "numPage must be more than or equals 0 ('ZERO')";
    public static final MsgBase SIZEPAGE_MUST_BE_MORE_THAN_OR_EQUALS_ZERO = () -> "sizePage must be more than or equals 0 ('ZERO')";
    public static final MsgBaseWithParams SIZEPAGE_MUST_BE_LESS_THAN_X  = "sizePage must be less than %s. try to paginate request"::formatted;
    
}
