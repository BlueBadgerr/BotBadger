import java.util.Random;

public class Summoner {
    private static final String[] HERO_3_STAR = {"Plitvice", "Lapice", "Marina", "Arabelle", "Eva", "Bari", "Lupina",
            "Lahn", "Eugene", "Tinia", "Vishuvac", "Nari", "Bianca", "Oghma", "Alef", "Miya", "Future Princess",
            "Garam", "Beth", "Rue", "Gabriel", "Lynn", "Future Knight", "Veronica", "Noxia", "Mayreel", "Mk.99",
            "Lilith", "Lucy", "Sohee", "Yuze", "Eleanor", "Scintilla", "Erina", "Kamael", "Mk.2"};

    private static final String[] HERO_2_STAR = {"Eva", "Elvira", "White Beast", "Karina", "Loraine", "Lavi", "Favi",
            "Aoba", "Gremory", "Rachel", "Hekate", "Coco", "Marianne", "Sohee", "Mei", "Marvin", "Craig", "Akayuki",
            "Ranpang", "Yuze", "Aisha", "Shapira", "Dolf", "Amy", "Girgas", "Catherine", "Rie", "Neva"};

    private static final String[] HERO_1_STAR = {"Linda", "Bob", "Hyper", "Maria", "Lisa", "Leah", "Jay", "Dragon",
            "Blade", "Mina", "Hoshida", "Peggy", "Allie", "Oralie", "Kang", "Agatha", " DaVinci", "Kate", "Zoe", "Rio",
            "Nyan", "Marty Junior"};

    private static final int CHANCE_3_STAR = 2_750;
    private static final int CHANCE_2_STAR = 19_000;
    private static final int TOTAL_PROBABILITY = 100_000;

    private Random random = new Random();

    public Summoner() {}

    public String getSummonResult() {
        StringBuilder sb = new StringBuilder();

        for(int i = 0;  i < 9; i ++) {
            int result = random.nextInt(TOTAL_PROBABILITY);

            if (result < CHANCE_3_STAR) {
                sb.append(String.format("**\\*\\*\\* %s**%n", HERO_3_STAR[random.nextInt(HERO_3_STAR.length)]));
            } else if (result < CHANCE_3_STAR + CHANCE_2_STAR) {
                sb.append(String.format("\\*\\* %s%n", HERO_2_STAR[random.nextInt(HERO_2_STAR.length)]));
            } else {
                sb.append(String.format("\\* %s%n", HERO_1_STAR[random.nextInt(HERO_1_STAR.length)]));
            }
        }

        // Perform guarantee 2* minimum
        int result = random.nextInt(TOTAL_PROBABILITY);

        if (result < CHANCE_3_STAR) {
            sb.append(String.format("**\\*\\*\\* %s**%n", HERO_3_STAR[random.nextInt(HERO_3_STAR.length)]));
        } else {
            sb.append(String.format("\\*\\* %s%n", HERO_2_STAR[random.nextInt(HERO_2_STAR.length)]));
        }

        return sb.toString();
    }
}
