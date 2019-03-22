package me.hekr.sthome.tools;

import java.util.HashSet;
import java.util.Set;
import me.hekr.sdk.Hekr;

/**
 * Created by ryanhsueh on 2019/3/22
 */
public class HekrSocketUtil {

    public static void setDefaultChannel() {
        // 使用集合Set，因为设备的host可能会有重复
        Set<String> hosts = new HashSet<>();
        hosts.add("fra-hub.hekreu.me");
        Hekr.getHekrClient().setHosts(hosts);
    }

}
