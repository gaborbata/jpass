/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpass.crypt;


/**
 *
 * @author ryoji
 */
public enum SaltHolder {
    INST {
        private byte[] salt;
        @Override
        public void setSalt(byte[] salt) {
            this.salt = salt;
        }

        @Override
        public byte[] getSalt() {
            return salt;
        }
    };
    public abstract void setSalt(byte[] salt);
    public abstract byte[] getSalt();
}
