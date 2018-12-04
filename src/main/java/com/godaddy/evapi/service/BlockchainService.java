package com.godaddy.evapi.service;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Value;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ManagedTransaction;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import com.godaddy.evapi.TestContract;
import com.godaddy.evapi.model.OrganizationModel;

public class BlockchainService implements IBlockchainService {
    @Value( "${blockchain.url}" )
    private String blockchainUrl;
    
    @Value( "${blockchain.contract.address}" )
    private String contractAddress;

    @Value( "${blockchain.account.address}" )
    private String accountAddress;
    
    @Value( "${blockchain.account.password}" )
    private String password;
    
    @Value( "${blockchain.wallet.file}" )
    private String walletFile;
    
    private Web3j web3j;
    private Credentials credentials;

    public static final BigInteger GAS_LIMIT = BigInteger.valueOf(500000);

    public BlockchainService() throws Exception {
        web3j = Web3j.build(new HttpService(blockchainUrl));
        credentials = WalletUtils.loadCredentials(password, walletFile);
    }
    
    // Create/Update
    // Probably want to pass in json data for this...
    public boolean save(OrganizationModel org) {
        // TODO: Calculate gas to use
        // TODO: Figure out how to send data along with transaction
        /*
        Admin web3j = Admin.build(new HttpService());
        PersonalUnlockAccount personalUnlockAccount = web3j.personalUnlockAccount("0x000...", "a password").send();
        if (personalUnlockAccount.accountUnlocked()) {
            // send a transaction
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                        address, DefaultBlockParameterName.LATEST).sendAsync().get();

            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            Transaction transaction = Transaction.createContractTransaction(
                        <from address>,
                        nonce,
                        BigInteger.valueOf(<gas price>),  // we use default gas limit
                        "0x...<smart contract code to execute>"
                );

            //RawTransaction rawTransaction  = RawTransaction.createEtherTransaction(
            //            nonce, <gas price>, <gas limit>, <toAddress>, <value>);

                org.web3j.protocol.core.methods.response.EthSendTransaction
                        transactionResponse = parity.ethSendTransaction(ethSendTransaction)
                        .send();

                String transactionHash = transactionResponse.getTransactionHash();

        }
        */
        return false;
    }
    
    // Delete
    public boolean delete(String id) {
        return false;
    }
    
    public void writeRecord(String id, String name) {
        try {
            TestContract contract = getContract();
            TransactionReceipt receipt = contract.createEntry(id, name).send();
        } catch (Exception ex) {
        }
        return;        
    }
    
    public int getLength() {
        try {
            TestContract contract = getContract();
            BigInteger value = contract.getLength().send();
            return value.intValue();
        } catch (Exception ex) {
            return -1;
        }
    }
    
    public String getRecord(String id) {
        try {
        TestContract contract = getContract();
        String value = contract.getEntry(id).send();
        return value;
        } catch (Exception ex) {
            return null;
        }
    }
    
    public String sendCurrency(String toAddress, BigInteger value, String jsonData) {
        /*
        blockchainUrl = "http://localhost:8383/";
        password = "EtherDuplicateMediumCelebration6789$!";
        walletFile = "/Users/asink/eth-chain-asink2/data/keystore/UTC--2018-09-27T17-06-35.740543226Z--7664c4c88722a070e96173c1f2a2ebf360756cfb";
        contractAddress = "0x31f2d665bc7b06b7f6333113331fb5da394ed4e6";
        accountAddress = "0x7664c4c88722a070e96173c1f2a2ebf360756cfb";
        */
        try {
            Web3j web3 = Web3j.build(new HttpService(blockchainUrl));
            Credentials credentials = WalletUtils.loadCredentials(password, walletFile);
            EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                        accountAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            // TODO: Remove this...
//            value = Convert.toWei("1.0", Convert.Unit.ETHER).toBigInteger();
            RawTransaction rawTransaction  = RawTransaction.createTransaction(nonce, ManagedTransaction.GAS_PRICE, GAS_LIMIT, toAddress, value, jsonData);
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction response = web3.ethSendRawTransaction(hexValue).send();

            // TODO: Not seeing input  in the transction when doing this...

            if (response.getError() != null) {
                System.out.println(response.getError().getMessage());
            }
            else {
                EthGetTransactionReceipt receipt = web3.ethGetTransactionReceipt(response.getTransactionHash()).send();
                if (receipt.getTransactionReceipt().isPresent()) {
                    TransactionReceipt r = receipt.getTransactionReceipt().get();
                }
                // Success! We can leave now.
                return response.getTransactionHash();
            }
        } catch (Exception ex) {
            //ex.getStackTrace().
        }
        
        // If we got here, something went wrong.
        return null;
    }
    
    // Read/Get
    public void findById(String id) {
        
    }
    
    public void findAll(int offset, int limit) {
        
    }
    
    public void findByCName(String commonName) {
        
    }
    
    public void findValidationItems(String id) {
        
    }
    
    public long getBlockNumber() {
        try {
            EthBlockNumber result = new EthBlockNumber();
            result = web3j.ethBlockNumber().sendAsync().get();
            return result.getBlockNumber().longValue();
        } catch (Exception ex) {
            return -1;
        }
    }
    
    public long getTransactionCount() {
        try {
            EthGetTransactionCount result = new EthGetTransactionCount();
            result = web3j.ethGetTransactionCount(accountAddress, DefaultBlockParameter.valueOf("latest")).sendAsync().get();
            return result.getTransactionCount().longValue();
        } catch (Exception ex) {
            return -1;
        }
    }
    
    public long getEthBalance() {
        try {
            EthGetBalance result = new EthGetBalance();
            result = web3j.ethGetBalance(accountAddress, DefaultBlockParameter.valueOf("latest")).sendAsync().get();
            return result.getBalance().longValue();
        } catch (Exception ex) {
            return -1;
        }
    }

    private TestContract getContract() throws Exception {
        /*
        blockchainUrl = "http://localhost:8383/";
        password = "EtherDuplicateMediumCelebration6789$!";
        walletFile = "/Users/asink/eth-chain-asink2/data/keystore/UTC--2018-09-27T17-06-35.740543226Z--7664c4c88722a070e96173c1f2a2ebf360756cfb";
        contractAddress = "0x31f2d665bc7b06b7f6333113331fb5da394ed4e6";
        */
        return TestContract.load(contractAddress, web3j, credentials, ManagedTransaction.GAS_PRICE, GAS_LIMIT);
    }
}
