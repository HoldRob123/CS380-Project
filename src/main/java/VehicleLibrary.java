import java.util.Random;

public class VehicleLibrary {

    Random r = new Random();

    public String getGeneralWrittenExplaination(String make, String model) {
        make = make.toUpperCase();
        model = model.toUpperCase();
        // Returns a written explaination for the compare feature
        switch (make) {
            // ----Popular Brands---
            case "FORD":
                if (model.startsWith("F-")) {
                    return "Ford's F-Series, encompassing models like the F-150 and Super Duty, \n" +
                            "continues its reign as America's best-selling vehicle for the 43rd consecutive year. Renowned for its durability and versatility, \n" +
                            "the F-Series offers a range of powertrains, including hybrid and electric options like the F-150 Lightning. \n" +
                            "Its impressive towing capacity and advanced tech features make it a favorite among both commercial and personal users. \n";
                }
                break;

            case "CHEVROLET":
                if (model.equals("SILVERADO")) {
                    return "The Silverado, Chevrolet's flagship pickup, stands out with its robust performance and diverse configurations. \n" +
                            "Offering powerful V8 engines and features like the Super Cruise semi-autonomous driving system, it caters to both work \n" +
                            "and leisure needs. Its reliability and towing prowess solidify its position in the competitive truck segment. \n";
                }
                break;

            case "TOYOTA":
                if (model.equals("RAV-4")) {
                    return "As the top-selling SUV in the U.S., the Toyota RAV4 combines reliability with versatility. \n" +
                            "Available in hybrid and plug-in hybrid variants, it boasts impressive fuel efficiency. \n" +
                            "Standard safety features and a spacious interior make it a preferred choice for families and commuters alike.\n";
                } else if (model.equals("CAMRY")) {
                    return "The Camry continues to dominate the midsize sedan segment, known for its dependability and fuel efficiency. \n" +
                            "With the introduction of hybrid models, it appeals to a broader audience seeking eco-friendly options. \n" +
                            "Its comfortable ride and advanced safety features contribute to its enduring popularity.\n";
                }
                break;

            case "HONDA":
                if (model.equals("CR-V")) {
                    return "Honda's CR-V offers a blend of comfort, efficiency, and practicality. \n" +
                            "With a significant portion of sales attributed to its hybrid models, it appeals to eco-conscious buyers. \n" +
                            "Its reputation for reliability and ample cargo space enhances its appeal in the compact SUV market. \n";
                } else if (model.equals("CIVIC")) {
                    return "The Civic, Honda's compact car, is celebrated for its reliability and engaging driving experience. \n" +
                            "Available in various trims, including sporty and hybrid versions, it caters to a wide range of preferences. \n" +
                            "Its efficient performance and modern design continue to attract both new and returning buyers. \n";
                }
                break;

            case "DODGE":
                if (model.equals("RAM")) {
                    return "Ram's pickup line, particularly the 1500 series, is celebrated for its blend of power and luxury. \n" +
                            "Features like the RamBox Cargo Management System and optional air suspension offer both utility and comfort. \n" +
                            "Despite a decline in sales, it remains a strong contender in the full-size truck segment.\n";
                }
                break;

            case "TESLA":
                if (model.equals("MODEL Y")) {
                    return "The Model Y, Tesla's compact SUV, leads in the electric vehicle market with its impressive range and performance. \n" +
                            "Features like Full Self-Driving capability and access to Tesla's Supercharger network enhance its appeal. \n" +
                            "Its sleek design and tech-forward approach attract a broad range of consumers. \n";
                }
                break;

            case "GMC":
                if (model.equals("SIERRA")) {
                    return "GMC's Sierra offers a premium take on the full-size pickup, sharing its platform with the Silverado \n" +
                            "but adding upscale touches. High-end trims like the Denali Ultimate feature luxury amenities, catering to buyers seeking \n" +
                            "both capability and comfort. Its reliability and advanced features make it a top choice in its class. \n";
                }
                break;

            case "NISSAN":
                if (model.equals("ROGUE")) {
                    return "Nissan's Rogue remains a strong player in the compact SUV market, offering a comfortable ride and user-friendly technology. \n" +
                            "Despite facing stiff competition and a slight sales decline, its fuel efficiency and spacious interior \n" +
                            "keep it appealing to families and daily commuters. \n";
                }
                break;

            // ----Defunct Brands---
            case "PONTIAC":
                return "Defunct in 2010, Pontiac was a division of General Motors known for its performance-oriented image and sporty styling. \n" +
                        "Models like the Firebird and GTO earned it a strong reputation during the muscle car era. \n" +
                        "Although reliability varied, Pontiac cars were often seen as more youthful and aggressive compared to their GM siblings. \n" +
                        "The brand struggled with identity and sales before being axed during GM’s 2009 restructuring.\n";

            case "SATURN":
                return "Created by General Motors in 1985 as a \"different kind of car company,\" Saturn aimed to compete with Japanese imports through \n" +
                        "innovation and no-haggle pricing. Early models were praised for decent reliability and customer satisfaction. \n" +
                        "However, a lack of consistent investment and a dated lineup ultimately led to its demise in 2010. Saturn is remembered for its \n" +
                        "ambitious launch and loyal customer base.\n";

            case "OLDSMOBILE":
                return "As one of America’s oldest car brands and another GM division, Oldsmobile had a long legacy of innovation, \n" +
                        "including the first mass-produced car, the Curved Dash. It was considered a reliable, mid-luxury option in its heyday, \n" +
                        "especially with models like the Cutlass and 88. Over time, it lost its identity, \n" +
                        "blending too closely with other GM lines, which led to a decline in relevance and sales. \n";

            case "SAAB":
                return "Originally an aircraft manufacturer from Sweden, Saab became known for quirky yet practical designs, \n" +
                        "turbocharged engines, and a focus on safety. Its cars, like the 900 and 9-3, had a cult following thanks to their \n" +
                        "unique engineering and dependable performance, especially in snowy climates. Despite loyal fans, poor management and \n" +
                        "inconsistent ownership (including GM) led to its eventual bankruptcy in 2011. \n";

            case "SCION":
                return "Scion was a Toyota sub-brand designed to attract younger buyers with affordable, customizable, and fuel-efficient cars. \n" +
                        "Models like the tC and xB stood out for their bold styling and Toyota-level reliability. However, overlapping with \n" +
                        "Toyota’s own lineup and shifting market preferences led to the brand’s quiet phase-out. Most of its popular models \n" +
                        "were rebranded under Toyota after the shutdown.\n";

            default:
                return "\n";
        }
        return "\n";
    }




    // Helper method to generate a year if none is provided for common make and models
    public int getRealisticYear(String make) {
        // Switch with defunct car makes
        switch (make) {
            case "Studebaker":
                return r.nextInt(1966 - 1852 + 1) + 1852;
            case "Oldsmobile":
                return r.nextInt(2004 - 1897 + 1) + 1897;
            case "Packard":
                return r.nextInt(1958 - 1899 + 1) + 1899;
            case "Pontiac":
                return r.nextInt(2010 - 1926 + 1) + 1926;
            case "DeSoto":
                return r.nextInt(1961 - 1928 + 1) + 1928;
            case "Plymouth":
                return r.nextInt(2001 - 1928 + 1) + 1928;
            case "Mercury":
                return r.nextInt(2011 - 1938 + 1) + 1938;
            case "Tucker":
                return r.nextInt(1950 - 1944 + 1) + 1944;
            case "Kaiser-Frazer":
                return r.nextInt(1951 - 1944 + 1) + 1944;
            case "AMC":
                return r.nextInt(1988 - 1954 + 1) + 1954;
            case "Edsel":
                return r.nextInt(1959 - 1957 + 1) + 1957;
            case "DeLorean":
                return r.nextInt(1982 - 1975 + 1) + 1975;
            case "Saturn":
                return r.nextInt(2010 - 1985 + 1) + 1985;
            case "Hummer":
                return r.nextInt(2010 - 1992 + 1) + 1992;
            default:
                return r.nextInt(2025-1990+1) + 1990;
        }
    }
}

/*
SOURCES
Faviconautoblog.com
2024's best-selling cars: Who came out on top?

diminishedvalueofgeorgia.com
Top 10 Most Sold Cars in the US for 2024 (Full List) | DVGA

motortrend.com
The Best-Selling Cars, SUVs, and Trucks of 2024

auto123.com
The 25 best-selling vehicles in the U.S. in 2024 | Car News | Auto123

auto123.com
The 25 top-selling vehicles in 2024 in the U.S. | Car News | Auto123

kbb.com
The 25 Best-Selling Cars of 2024 So Far - Kelley Blue Book
Home General The best-selling cars, trucks, and SUVs of 2024 are off to a strong start, with most segments seeing about a 7 percent uptick in sales compared to the first half of 2023, which likewise...

motortrend.com
The Best-Selling Cars, SUVs, and Trucks of 2024

autoblog.com
2024's best-selling cars: Who came out on top?

auto123.com
The 25 best-selling vehicles in the U.S. in 2024 | Car News | Auto123

auto123.com
The 25 top-selling vehicles in 2024 in the U.S. | Car News | Auto123

americancarsandracing.com
The 10 Best Selling Vehicles Of 2024 In The USA | American Cars And Racing

marklines.com
USA - Flash report, Automotive sales volume, 2024 - MarkLines Automotive Industry Portal

diminishedvalueofgeorgia.com
Top 10 Most Sold Cars in the US for 2024 (Full List) | DVGA

webuyanycarusa.com
Driven to Success: The Best-Selling Cars of 2024! - We Buy Any Car®

daxstreet.com
Top 10 Best-Selling Cars of 2024 Showcase Market Recovery and Evolving Consumer Preferences - DAX Street

*/