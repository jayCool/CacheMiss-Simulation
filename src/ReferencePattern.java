/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author JIANGWEI
 */
public class ReferencePattern {
   public int node_id;

    public long getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }
   
   public long timestamp;

    public int getNode_id() {
        return node_id;
    }

    public String getObject() {
        return object;
    }

    public long getObject_id() {
        return object_id;
    }

    public int getChildID() {
        return childID;
    }

    public int getParent_id() {
        return parent_id;
    }

    public int getHitNode() {
        return hitNode;
    }

    public int getClient_id() {
        return client_id;
    }
   public String object;
   private int objSize = 1 ;
   public int status = 0; //0 false, 1 waiting, 2 solved
   public long object_id;
   public int childID=-2;
   public int parent_id = -2;
   public int hitNode = -2; 

    public void setNode_id(int node_id) {
        this.node_id = node_id;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setObject_id(long object_id) {
        this.object_id = object_id;
    }

    public void setChildID(int childID) {
        this.childID = childID;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public void setHitNode(int hitNode) {
        this.hitNode = hitNode;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }
   public int client_id ;
   

    public ReferencePattern(int node_id, long timestamp, String object, long object_id, int objectSize) {
        this.node_id = node_id;
        this.timestamp = timestamp;
        this.object = object;
        this.object_id = object_id;
        this.objSize = objectSize;
    }

    public void setObjSize(int objSize) {
        this.objSize = objSize;
    }

    public int getObjSize() {
        return objSize;
    }
    
    

    @Override
    public String toString() {
        return "ReferencePattern{" + "node_id=" + node_id + ", timestamp=" + timestamp + ", object=" + object + ", status=" + status + ", object_id=" + object_id + ", child_id=" + childID + ", parent_id=" + parent_id + ", hitNode=" + hitNode + ", client_id=" + client_id + '}';
    }

    
}
