/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.wallets.bitcoind.rpc;

import bisq.wallets.bitcoind.rpc.calls.*;
import bisq.wallets.bitcoind.rpc.responses.BitcoindFinalizePsbtResponse;
import bisq.wallets.rpc.RpcClient;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BitcoindDaemon {

    protected final RpcClient rpcClient;

    public BitcoindDaemon(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public void createOrLoadWallet(Path walletPath, Optional<String> passphrase) {
        if (!doesWalletExist(walletPath)) {
            createWallet(walletPath, passphrase.orElse(""));
        } else {
            List<String> loadedWallets = listWallets();
            if (!loadedWallets.contains(walletPath.toString())) {
                loadWallet(walletPath);
            }
        }
    }

    public BitcoindFinalizePsbtResponse finalizePsbt(String psbt) {
        var request = new BitcoindFinalizePsbtRpcCall.Request(psbt);
        var rpcCall = new BitcoindFinalizePsbtRpcCall(request);
        return rpcClient.invokeAndValidate(rpcCall);
    }

    public List<String> generateToAddress(int numberOfBlocksToMine, String addressOfMiner) {
        var request = BitcoindGenerateToAddressRpcCall.Request.builder()
                .nblocks(numberOfBlocksToMine)
                .address(addressOfMiner)
                .build();
        var rpcCall = new BitcoindGenerateToAddressRpcCall(request);
        String[] blockHashes = rpcClient.invokeAndValidate(rpcCall);
        return Arrays.asList(blockHashes);
    }

    public String getRawTransaction(String txId) {
        var request = new BitcoindGetRawTransactionRpcCall.Request(txId);
        var rpcCall = new BitcoindGetRawTransactionRpcCall(request);
        return rpcClient.invokeAndValidate(rpcCall);
    }

    public String getTxOutProof(List<String> txIds) {
        var request = new BitcoindGetTxOutProofRpcCall.Request(txIds);
        var rpcCall = new BitcoindGetTxOutProofRpcCall(request);
        return rpcClient.invokeAndValidate(rpcCall);
    }

    public List<String> listWallets() {
        var rpcCall = new BitcoindListWalletsRpcCall();
        String[] wallets = rpcClient.invokeAndValidate(rpcCall);
        return Arrays.asList(wallets);
    }

    public String sendRawTransaction(String hexString) {
        var request = new BitcoindSendRawTransactionRpcCall.Request(hexString);
        var rpcCall = new BitcoindSendRawTransactionRpcCall(request);
        return rpcClient.invokeAndValidate(rpcCall);
    }

    public void stop() {
        var rpcCall = new BitcoindStopRpcCall();
        rpcClient.invokeAndValidate(rpcCall);
    }

    public void unloadWallet(Path walletPath) {
        String absoluteWalletPath = walletPath.toAbsolutePath().toString();
        var request = new BitcoindUnloadWalletRpcCall.Request(absoluteWalletPath);
        var rpcCall = new BitcoindUnloadWalletRpcCall(request);
        rpcClient.invokeAndValidate(rpcCall);
    }

    private boolean doesWalletExist(Path walletPath) {
        return walletPath.toFile().exists();
    }

    private void createWallet(Path walletPath, String passphrase) {
        var request = BitcoindCreateWalletRpcCall.Request.builder()
                .walletName(walletPath.toAbsolutePath().toString())
                .passphrase(passphrase)
                .build();

        var rpcCall = new BitcoindCreateWalletRpcCall(request);
        rpcClient.invokeAndValidate(rpcCall);
    }

    private void loadWallet(Path walletPath) {
        String absoluteWalletPath = walletPath.toAbsolutePath().toString();
        var request = new BitcoindLoadWalletRpcCall.Request(absoluteWalletPath);
        var rpcCall = new BitcoindLoadWalletRpcCall(request);
        rpcClient.invokeAndValidate(rpcCall);
    }
}
