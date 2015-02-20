package com.crauterb.wifijedi.Util;

import libsvm.LibSVM;

/**
 * Created by christoph on 11.02.15.
 */
public class RSSISVM extends LibSVM {

    private String ownMacAdress;

    public String getOwnMacAdress() {
        return ownMacAdress;
    }

    public void setOwnMacAdress(String ownMacAdress) {
        this.ownMacAdress = ownMacAdress;
    }

    public RSSISVM(){
        super();
        this.ownMacAdress = "";

    }

    public RSSISVM(String ownMacAdress) {
        super();
        this.ownMacAdress = ownMacAdress;
    }
}
