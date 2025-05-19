package uk.co.perspective.app.helpers;

public class Utilities {

    public static String GetCurrencySymbol(String CurrencyName)
    {
        String symbol = "£";

        switch (CurrencyName)
        {
            case "Pounds Sterling":
                symbol = "£";
                break;

            case "Euro":
                symbol = "€";
                break;

            case "US Dollar":
                symbol = "$";
                break;

            case "Indian Rupee":
                symbol = "₹";
                break;

            case "Australian Dollar":
                symbol = "$";
                break;

            case "Canadian Dollar":
                symbol = "$";
                break;

            case "Singapore Dollar":
                symbol = "$";
                break;

            case "Swiss Franc":
                symbol = "CHF";
                break;

            case "Malaysian Ringgit":
                symbol = "RM";
                break;

            case "Japanese Yen":
                symbol = "¥";
                break;

            case "Chinese Yuan Renminbi":
                symbol = "¥";
                break;

            case "Saudi Arabia Riyal":
                symbol = "﷼";
                break;

            case "South African Rand":
                symbol = "R";
                break;

            default:
                symbol = "£";
                break;
        }

        return symbol;
    }
}
