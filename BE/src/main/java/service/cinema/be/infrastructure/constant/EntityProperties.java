package service.cinema.be.infrastructure.constant;

public class EntityProperties {

    private EntityProperties() {
    }

    public static final byte LENGTH_ID = 36;

    public static final byte LENGTH_CODE = 50;

    public static final short LENGTH_NAME = 255;

    public static final short LENGTH_PICTURE = 15000;

    public static final short LENGTH_URL = 2000;

    public static final short LENGTH_DESCRIPTION = 5000;

    public static final short LENGTH_CONTENT = 2000;

    // Entity Status
    public static final int HOAT_DONG = 0;
    public static final int NGUNG_HOAT_DONG = 1;
    public static final int DA_XOA = 2;

    // Gender Constants
    public static final String MALE = "MALE";
    public static final String FEMALE = "FEMALE";
    public static final String OTHER = "OTHER";

    // Payment Method Constants
    public static final String PAYMENT_CASH = "CASH";
    public static final String PAYMENT_CREDIT_CARD = "CREDIT_CARD";
    public static final String PAYMENT_VNPAY = "VNPAY";
    public static final String PAYMENT_MOMO = "MOMO";
    public static final String PAYMENT_ZALOPAY = "ZALOPAY";

    // Discount Type Constants
    public static final String DISCOUNT_PERCENTAGE = "PERCENTAGE";
    public static final String DISCOUNT_FIXED_AMOUNT = "FIXED_AMOUNT";

    // Review Rating Constants
    public static final int MIN_RATING = 1;
    public static final int MAX_RATING = 5;

    // Boolean State Constants
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    public static final boolean AVAILABLE = true;
    public static final boolean UNAVAILABLE = false;
    public static final boolean SHOWING = true;
    public static final boolean NOT_SHOWING = false;
    public static final boolean PUBLISHED = true;
    public static final boolean UNPUBLISHED = false;

}
