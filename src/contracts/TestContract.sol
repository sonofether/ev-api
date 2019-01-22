pragma solidity ^0.4.7;

contract TestContract {
    struct TestItem {
        string name;
        bool initialized;
    }
 
    uint256 private length;
    mapping(string => TestItem) private itemMap;
    bool private lock;
    
    function createEntry(string id, string name) public returns(bool) {
        if(itemMap[id].initialized) {
            return false;
        }
        
        require(!lock);
        lock = true;
        TestItem storage item = itemMap[id];
        item.initialized = true;
        item.name = name;
        length += 1;
        lock = false;
        
        return true;
    }
    
    function getEntry(string id) public view returns(string) {
        if(itemMap[id].initialized) {
            return itemMap[id].name;
        }
        
        return "";
    }
    
    function getLength() public view returns(uint256) {
        return length;
    }
    
    function testFunction() public pure returns(string) {
        return "Test Successful";
    }
}