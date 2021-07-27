package de.superchat.message.substitution;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BTCSubstitution implements Substitution {

    /**
     * TODO: Better use distribution cache like Redis + background jobs to update.
     */
    static Cache<String, Double> oneBTCPriceCache;

    private synchronized void initCache() {
        if (oneBTCPriceCache != null) {
            return;
        }

        CacheLoader<String, Double> loader;
        loader = new CacheLoader<>() {
            @Override
            public Double load(String key) {
                return generateFakeBTCPrice();
            }
        };

        oneBTCPriceCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(loader);
    }

    @Override
    public String substitute(String placeholder) {
        initCache();

        String btc = placeholder.replace("BTC", "");
        double btcInDouble = Double.parseDouble(btc);
        Double oneBTCInUSDT = oneBTCPriceCache.getUnchecked("1BTC");
        double val = (btcInDouble * oneBTCInUSDT);
        return "$" + new BigDecimal(val).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getPlaceholderPattern() {
        return "BTC[0-9]+([.][0-9]*)?|[.][0-9]+";
    }

    /**
     * I am too lazy to do real network call :(.
     */
    private static Double generateFakeBTCPrice() {
        return 30000D + new Random().nextDouble() * (60000D - 30000D);
    }
}
