package com.cryptoapp.subspace;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

/**
 * Created by DestinationX on 10/2/2018.
 */

public class Constants {

    public static final boolean TEST = true;

    /** Network this wallet is on (e.g. testnet or mainnet). */
    public static final NetworkParameters NETWORK_PARAMETERS = TEST ? TestNet3Params.get() : MainNetParams.get();

    //Earliestkeycreationtime == min earliest time that photographic wallet has been created..should be late as possibly can be
    public static final long earliestKeyCreationTime = 1537142400L;
}
