/*
 * Copyright (c) 2007, Timm Knape
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of Timm Knape nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package jpass.crypt;

/**
 * Implementation of the &quot;Advanced Encryption Standard&quot; (AES) with a key size of 256 bit.
 * The specification in &quot;Federal Information Processing Standards Publication 197&quot; defines
 * the following key sizes:
 * <ul>
 * <li>128 bit,</li>
 * <li>192 bit,</li>
 * <li>256 bit.</li>
 * </ul>
 * To make this implementation more compact, only the strongest version will be implemented.
 * <p>
 * The block size is 128 bit.
 * </p>
 *
 * @author Timm Knape
 * @version $Revision: 1.3 $
 */
public final class Aes256 {

    /**
     * Number of {@code byte}s needed for 32 bit words.
     */
    private static final int WORD_SIZE = 4;

    /**
     * Number of {@code byte}s for a data block. The size is identical to the size of the internal
     * state, that is needed in encryption or decryption.
     * <p>
     * The state can be viewed as a square matrix, modeled as a list of column vectors.
     * </p>
     */
    private static final int BLOCK_SIZE = 16;

    /**
     * key size in {@code byte}s
     */
    private static final int KEY_SIZE = 32;

    /**
     * number of rounds
     */
    private static final int ROUNDS = 14;

    /**
     * Size of the expanded key. For each round a {@code BLOCK_SIZE} block will be needed. Before
     * the first round another block will be needed.
     */
    private static final int EXPANDED_KEY_SIZE = (ROUNDS + 1) * BLOCK_SIZE;

    /**
     * expanded key
     */
    private final byte[] _expandedKey;

    /**
     * state needed between rounds in en- or decryption
     */
    private final byte[] _tmp;

    /**
     * Permutation of {@code byte}s. The s-box permutation was specified in the reference document.
     */
    private final byte[] _sBox = {(byte) 0x63, (byte) 0x7c, (byte) 0x77,
        (byte) 0x7b, (byte) 0xf2, (byte) 0x6b, (byte) 0x6f, (byte) 0xc5,
        (byte) 0x30, (byte) 0x01, (byte) 0x67, (byte) 0x2b, (byte) 0xfe,
        (byte) 0xd7, (byte) 0xab, (byte) 0x76, (byte) 0xca, (byte) 0x82,
        (byte) 0xc9, (byte) 0x7d, (byte) 0xfa, (byte) 0x59, (byte) 0x47,
        (byte) 0xf0, (byte) 0xad, (byte) 0xd4, (byte) 0xa2, (byte) 0xaf,
        (byte) 0x9c, (byte) 0xa4, (byte) 0x72, (byte) 0xc0, (byte) 0xb7,
        (byte) 0xfd, (byte) 0x93, (byte) 0x26, (byte) 0x36, (byte) 0x3f,
        (byte) 0xf7, (byte) 0xcc, (byte) 0x34, (byte) 0xa5, (byte) 0xe5,
        (byte) 0xf1, (byte) 0x71, (byte) 0xd8, (byte) 0x31, (byte) 0x15,
        (byte) 0x04, (byte) 0xc7, (byte) 0x23, (byte) 0xc3, (byte) 0x18,
        (byte) 0x96, (byte) 0x05, (byte) 0x9a, (byte) 0x07, (byte) 0x12,
        (byte) 0x80, (byte) 0xe2, (byte) 0xeb, (byte) 0x27, (byte) 0xb2,
        (byte) 0x75, (byte) 0x09, (byte) 0x83, (byte) 0x2c, (byte) 0x1a,
        (byte) 0x1b, (byte) 0x6e, (byte) 0x5a, (byte) 0xa0, (byte) 0x52,
        (byte) 0x3b, (byte) 0xd6, (byte) 0xb3, (byte) 0x29, (byte) 0xe3,
        (byte) 0x2f, (byte) 0x84, (byte) 0x53, (byte) 0xd1, (byte) 0x00,
        (byte) 0xed, (byte) 0x20, (byte) 0xfc, (byte) 0xb1, (byte) 0x5b,
        (byte) 0x6a, (byte) 0xcb, (byte) 0xbe, (byte) 0x39, (byte) 0x4a,
        (byte) 0x4c, (byte) 0x58, (byte) 0xcf, (byte) 0xd0, (byte) 0xef,
        (byte) 0xaa, (byte) 0xfb, (byte) 0x43, (byte) 0x4d, (byte) 0x33,
        (byte) 0x85, (byte) 0x45, (byte) 0xf9, (byte) 0x02, (byte) 0x7f,
        (byte) 0x50, (byte) 0x3c, (byte) 0x9f, (byte) 0xa8, (byte) 0x51,
        (byte) 0xa3, (byte) 0x40, (byte) 0x8f, (byte) 0x92, (byte) 0x9d,
        (byte) 0x38, (byte) 0xf5, (byte) 0xbc, (byte) 0xb6, (byte) 0xda,
        (byte) 0x21, (byte) 0x10, (byte) 0xff, (byte) 0xf3, (byte) 0xd2,
        (byte) 0xcd, (byte) 0x0c, (byte) 0x13, (byte) 0xec, (byte) 0x5f,
        (byte) 0x97, (byte) 0x44, (byte) 0x17, (byte) 0xc4, (byte) 0xa7,
        (byte) 0x7e, (byte) 0x3d, (byte) 0x64, (byte) 0x5d, (byte) 0x19,
        (byte) 0x73, (byte) 0x60, (byte) 0x81, (byte) 0x4f, (byte) 0xdc,
        (byte) 0x22, (byte) 0x2a, (byte) 0x90, (byte) 0x88, (byte) 0x46,
        (byte) 0xee, (byte) 0xb8, (byte) 0x14, (byte) 0xde, (byte) 0x5e,
        (byte) 0x0b, (byte) 0xdb, (byte) 0xe0, (byte) 0x32, (byte) 0x3a,
        (byte) 0x0a, (byte) 0x49, (byte) 0x06, (byte) 0x24, (byte) 0x5c,
        (byte) 0xc2, (byte) 0xd3, (byte) 0xac, (byte) 0x62, (byte) 0x91,
        (byte) 0x95, (byte) 0xe4, (byte) 0x79, (byte) 0xe7, (byte) 0xc8,
        (byte) 0x37, (byte) 0x6d, (byte) 0x8d, (byte) 0xd5, (byte) 0x4e,
        (byte) 0xa9, (byte) 0x6c, (byte) 0x56, (byte) 0xf4, (byte) 0xea,
        (byte) 0x65, (byte) 0x7a, (byte) 0xae, (byte) 0x08, (byte) 0xba,
        (byte) 0x78, (byte) 0x25, (byte) 0x2e, (byte) 0x1c, (byte) 0xa6,
        (byte) 0xb4, (byte) 0xc6, (byte) 0xe8, (byte) 0xdd, (byte) 0x74,
        (byte) 0x1f, (byte) 0x4b, (byte) 0xbd, (byte) 0x8b, (byte) 0x8a,
        (byte) 0x70, (byte) 0x3e, (byte) 0xb5, (byte) 0x66, (byte) 0x48,
        (byte) 0x03, (byte) 0xf6, (byte) 0x0e, (byte) 0x61, (byte) 0x35,
        (byte) 0x57, (byte) 0xb9, (byte) 0x86, (byte) 0xc1, (byte) 0x1d,
        (byte) 0x9e, (byte) 0xe1, (byte) 0xf8, (byte) 0x98, (byte) 0x11,
        (byte) 0x69, (byte) 0xd9, (byte) 0x8e, (byte) 0x94, (byte) 0x9b,
        (byte) 0x1e, (byte) 0x87, (byte) 0xe9, (byte) 0xce, (byte) 0x55,
        (byte) 0x28, (byte) 0xdf, (byte) 0x8c, (byte) 0xa1, (byte) 0x89,
        (byte) 0x0d, (byte) 0xbf, (byte) 0xe6, (byte) 0x42, (byte) 0x68,
        (byte) 0x41, (byte) 0x99, (byte) 0x2d, (byte) 0x0f, (byte) 0xb0,
        (byte) 0x54, (byte) 0xbb, (byte) 0x16};

    /**
     * Inverse of the s-box permutation.
     */
    private final byte[] _invSBox = {(byte) 0x52, (byte) 0x09, (byte) 0x6a,
        (byte) 0xd5, (byte) 0x30, (byte) 0x36, (byte) 0xa5, (byte) 0x38,
        (byte) 0xbf, (byte) 0x40, (byte) 0xa3, (byte) 0x9e, (byte) 0x81,
        (byte) 0xf3, (byte) 0xd7, (byte) 0xfb, (byte) 0x7c, (byte) 0xe3,
        (byte) 0x39, (byte) 0x82, (byte) 0x9b, (byte) 0x2f, (byte) 0xff,
        (byte) 0x87, (byte) 0x34, (byte) 0x8e, (byte) 0x43, (byte) 0x44,
        (byte) 0xc4, (byte) 0xde, (byte) 0xe9, (byte) 0xcb, (byte) 0x54,
        (byte) 0x7b, (byte) 0x94, (byte) 0x32, (byte) 0xa6, (byte) 0xc2,
        (byte) 0x23, (byte) 0x3d, (byte) 0xee, (byte) 0x4c, (byte) 0x95,
        (byte) 0x0b, (byte) 0x42, (byte) 0xfa, (byte) 0xc3, (byte) 0x4e,
        (byte) 0x08, (byte) 0x2e, (byte) 0xa1, (byte) 0x66, (byte) 0x28,
        (byte) 0xd9, (byte) 0x24, (byte) 0xb2, (byte) 0x76, (byte) 0x5b,
        (byte) 0xa2, (byte) 0x49, (byte) 0x6d, (byte) 0x8b, (byte) 0xd1,
        (byte) 0x25, (byte) 0x72, (byte) 0xf8, (byte) 0xf6, (byte) 0x64,
        (byte) 0x86, (byte) 0x68, (byte) 0x98, (byte) 0x16, (byte) 0xd4,
        (byte) 0xa4, (byte) 0x5c, (byte) 0xcc, (byte) 0x5d, (byte) 0x65,
        (byte) 0xb6, (byte) 0x92, (byte) 0x6c, (byte) 0x70, (byte) 0x48,
        (byte) 0x50, (byte) 0xfd, (byte) 0xed, (byte) 0xb9, (byte) 0xda,
        (byte) 0x5e, (byte) 0x15, (byte) 0x46, (byte) 0x57, (byte) 0xa7,
        (byte) 0x8d, (byte) 0x9d, (byte) 0x84, (byte) 0x90, (byte) 0xd8,
        (byte) 0xab, (byte) 0x00, (byte) 0x8c, (byte) 0xbc, (byte) 0xd3,
        (byte) 0x0a, (byte) 0xf7, (byte) 0xe4, (byte) 0x58, (byte) 0x05,
        (byte) 0xb8, (byte) 0xb3, (byte) 0x45, (byte) 0x06, (byte) 0xd0,
        (byte) 0x2c, (byte) 0x1e, (byte) 0x8f, (byte) 0xca, (byte) 0x3f,
        (byte) 0x0f, (byte) 0x02, (byte) 0xc1, (byte) 0xaf, (byte) 0xbd,
        (byte) 0x03, (byte) 0x01, (byte) 0x13, (byte) 0x8a, (byte) 0x6b,
        (byte) 0x3a, (byte) 0x91, (byte) 0x11, (byte) 0x41, (byte) 0x4f,
        (byte) 0x67, (byte) 0xdc, (byte) 0xea, (byte) 0x97, (byte) 0xf2,
        (byte) 0xcf, (byte) 0xce, (byte) 0xf0, (byte) 0xb4, (byte) 0xe6,
        (byte) 0x73, (byte) 0x96, (byte) 0xac, (byte) 0x74, (byte) 0x22,
        (byte) 0xe7, (byte) 0xad, (byte) 0x35, (byte) 0x85, (byte) 0xe2,
        (byte) 0xf9, (byte) 0x37, (byte) 0xe8, (byte) 0x1c, (byte) 0x75,
        (byte) 0xdf, (byte) 0x6e, (byte) 0x47, (byte) 0xf1, (byte) 0x1a,
        (byte) 0x71, (byte) 0x1d, (byte) 0x29, (byte) 0xc5, (byte) 0x89,
        (byte) 0x6f, (byte) 0xb7, (byte) 0x62, (byte) 0x0e, (byte) 0xaa,
        (byte) 0x18, (byte) 0xbe, (byte) 0x1b, (byte) 0xfc, (byte) 0x56,
        (byte) 0x3e, (byte) 0x4b, (byte) 0xc6, (byte) 0xd2, (byte) 0x79,
        (byte) 0x20, (byte) 0x9a, (byte) 0xdb, (byte) 0xc0, (byte) 0xfe,
        (byte) 0x78, (byte) 0xcd, (byte) 0x5a, (byte) 0xf4, (byte) 0x1f,
        (byte) 0xdd, (byte) 0xa8, (byte) 0x33, (byte) 0x88, (byte) 0x07,
        (byte) 0xc7, (byte) 0x31, (byte) 0xb1, (byte) 0x12, (byte) 0x10,
        (byte) 0x59, (byte) 0x27, (byte) 0x80, (byte) 0xec, (byte) 0x5f,
        (byte) 0x60, (byte) 0x51, (byte) 0x7f, (byte) 0xa9, (byte) 0x19,
        (byte) 0xb5, (byte) 0x4a, (byte) 0x0d, (byte) 0x2d, (byte) 0xe5,
        (byte) 0x7a, (byte) 0x9f, (byte) 0x93, (byte) 0xc9, (byte) 0x9c,
        (byte) 0xef, (byte) 0xa0, (byte) 0xe0, (byte) 0x3b, (byte) 0x4d,
        (byte) 0xae, (byte) 0x2a, (byte) 0xf5, (byte) 0xb0, (byte) 0xc8,
        (byte) 0xeb, (byte) 0xbb, (byte) 0x3c, (byte) 0x83, (byte) 0x53,
        (byte) 0x99, (byte) 0x61, (byte) 0x17, (byte) 0x2b, (byte) 0x04,
        (byte) 0x7e, (byte) 0xba, (byte) 0x77, (byte) 0xd6, (byte) 0x26,
        (byte) 0xe1, (byte) 0x69, (byte) 0x14, (byte) 0x63, (byte) 0x55,
        (byte) 0x21, (byte) 0x0c, (byte) 0x7d};

    /**
     * Substitutes all {@code byte}s in a word. The word array will be changed.
     *
     * @param value array in which the first {@code WORD_SIZE} {@code byte}s will be substituted.
     * This array will be modified.
     * @return returns the modified {@code value}
     */
    private byte[] substituteWord(byte[] value) {
        for (int i = 0; i < WORD_SIZE; ++i) {
            value[i] = this._sBox[value[i] & 0xff];
        }
        return value;
    }

    /**
     * Rotate the {@code byte}'s in a word. The {@code byte}'s will be cycled left by one
     * {@code byte}. The modification will be in place, so the original argument is changed after
     * the method invocation.
     *
     * @param value Array in which the first {@code WORD_SIZE} {@code byte}'s will be changed due to
     * the rotation. The contents of this array is changed by this invocation.
     */
    private byte[] rotate(byte[] value) {
        byte tmp = value[0];
        for (int i = 1; i < WORD_SIZE; ++i) {
            value[i - 1] = value[i];
        }
        value[WORD_SIZE - 1] = tmp;
        return value;
    }

    /**
     * Expands the key. The incoming key is {@code KEY_SIZE} {@code byte}s long. It will be expanded
     * to a length of {@code EXPANDED_KEY_SIZE} {@code byte}s. The expanded key will be stored in
     * {@link Aes256#_expandedKey}.
     * <p>
     * The encryption and decryption will use the expanded key.
     * </p>
     *
     * @param key key for the AES algorithm
     */
    public Aes256(byte[] key) {
        this._expandedKey = new byte[EXPANDED_KEY_SIZE];
        this._tmp = new byte[BLOCK_SIZE];

        System.arraycopy(key, 0, this._expandedKey, 0, KEY_SIZE);

        for (int i = KEY_SIZE; i < EXPANDED_KEY_SIZE; i += WORD_SIZE) {
            System.arraycopy(this._expandedKey, i - WORD_SIZE, this._tmp, 0, WORD_SIZE);

            if (i % KEY_SIZE == 0) {
                substituteWord(rotate(this._tmp));
                this._tmp[0] ^= 1 << (i / KEY_SIZE - 1);
            } else if (i % KEY_SIZE == BLOCK_SIZE) {
                substituteWord(this._tmp);
            }

            for (int j = 0; j < WORD_SIZE; ++j) {
                this._expandedKey[i + j] = (byte) (this._expandedKey[i - KEY_SIZE + j] ^ this._tmp[j]);
            }
        }
    }

    /**
     * Combines the state with the expanded key. The {@code byte}s will be combined by {@code XOR}.
     *
     * @param index start of the part of the expanded key, that will be used for the combination
     */
    private void addRoundKey(int index) {
        for (int i = 0; i < BLOCK_SIZE; ++i) {
            this._tmp[i] = (byte) (this._tmp[i] ^ this._expandedKey[index + i]);
        }
    }

    /**
     * The polynomial represented by {@code b} will be multiplied by its free variable. This
     * multiplication takes place in a finite field. The resulting polynomial can still be represented
     * in one {@code byte}.
     * <p>
     * The bits {@code 0} to {@code 7} are the coefficients of the powers {@code x} to {@code x**8}.
     * </p>
     *
     * @param b origin polynomial
     *
     * @return multiplied polynomial
     */
    private int times2(int b) {
        int result = b << 1;
        if ((b & 0x80) != 0) {
            result ^= 0x1b;
        }
        return result & 0xff;
    }

    /**
     * Two polynomial will be multiplied with each other. The representation of the polynomial is
     * described in {@link Aes256#times2}.
     * <p>
     * The multiplication will be performed by successive invocations of {@link Aes256#times2}.
     * </p>
     *
     * @param a first polynomial
     * @param b second polynomial
     *
     * @return result of the multiplication
     */
    private byte mul(int a, byte b) {
        int result = 0;
        int first = a;
        int current = b & 0xff;
        while (first != 0) {
            if ((first & 0x01) != 0) {
                result ^= current;
            }
            first >>= 1;
            current = times2(current);
        }
        return (byte) (result & 0xff);
    }

    /**
     * Changes all {@code byte}s in the state by the s-box.
     */
    private void substituteState() {
        for (int i = 0; i < BLOCK_SIZE; ++i) {
            this._tmp[i] = this._sBox[this._tmp[i] & 0xff];
        }
    }

    /**
     * Rotates the last three rows of the state.
     */
    private void shiftRows() {
        byte tmp = this._tmp[1];
        this._tmp[1] = this._tmp[5];
        this._tmp[5] = this._tmp[9];
        this._tmp[9] = this._tmp[13];
        this._tmp[13] = tmp;

        tmp = this._tmp[2];
        this._tmp[2] = this._tmp[10];
        this._tmp[10] = tmp;
        tmp = this._tmp[6];
        this._tmp[6] = this._tmp[14];
        this._tmp[14] = tmp;

        tmp = this._tmp[3];
        this._tmp[3] = this._tmp[15];
        this._tmp[15] = this._tmp[11];
        this._tmp[11] = this._tmp[7];
        this._tmp[7] = tmp;
    }

    /**
     * Mixes one column of the state.
     *
     * @param index position of the first element of the column
     */
    private void mixColumn(int index) {
        int s0 = mul(2, this._tmp[index]) ^ mul(3, this._tmp[index + 1])
                ^ (this._tmp[index + 2] & 0xff) ^ (this._tmp[index + 3] & 0xff);
        int s1 = (this._tmp[index] & 0xff) ^ mul(2, this._tmp[index + 1])
                ^ mul(3, this._tmp[index + 2]) ^ (this._tmp[index + 3] & 0xff);
        int s2 = (this._tmp[index] & 0xff) ^ (this._tmp[index + 1] & 0xff)
                ^ mul(2, this._tmp[index + 2]) ^ mul(3, this._tmp[index + 3]);
        int s3 = mul(3, this._tmp[index]) ^ (this._tmp[index + 1] & 0xff)
                ^ (this._tmp[index + 2] & 0xff) ^ mul(2, this._tmp[index + 3]);
        this._tmp[index] = (byte) (s0 & 0xff);
        this._tmp[index + 1] = (byte) (s1 & 0xff);
        this._tmp[index + 2] = (byte) (s2 & 0xff);
        this._tmp[index + 3] = (byte) (s3 & 0xff);
    }

    /**
     * Mixes all columns of the state.
     */
    private void mixColumns() {
        mixColumn(0);
        mixColumn(4);
        mixColumn(8);
        mixColumn(12);
    }

    /**
     * Encrypts one block. The input block lies in {@code inBlock} starting at the position
     * {@code inIndex}. The {@code inBlock} won't be modified by this method. The encrypted block
     * will be stored in {@code outBlock} starting at position {@code outIndex}.
     *
     * @param inBlock array containing the input block
     * @param inIndex starting of the input block in {@code inBlock}
     * @param outBlock array to store the encrypted block
     * @param outIndex starting of the encrypted block in {@code outBlock}
     */
    public void encrypt(byte[] inBlock, int inIndex, byte[] outBlock,
            int outIndex) {
        System.arraycopy(inBlock, inIndex, this._tmp, 0, BLOCK_SIZE);

        addRoundKey(0);
        for (int round = 1; round < ROUNDS; ++round) {
            substituteState();
            shiftRows();
            mixColumns();
            addRoundKey(round * BLOCK_SIZE);
        }

        substituteState();
        shiftRows();
        addRoundKey(ROUNDS * BLOCK_SIZE);

        System.arraycopy(this._tmp, 0, outBlock, outIndex, BLOCK_SIZE);
    }

    /**
     * Rotates the last three rows of the state. This method inverses {@link Aes256#shiftRows}.
     */
    private void invShiftRows() {
        byte tmp = this._tmp[13];
        this._tmp[13] = this._tmp[9];
        this._tmp[9] = this._tmp[5];
        this._tmp[5] = this._tmp[1];
        this._tmp[1] = tmp;

        tmp = this._tmp[2];
        this._tmp[2] = this._tmp[10];
        this._tmp[10] = tmp;
        tmp = this._tmp[6];
        this._tmp[6] = this._tmp[14];
        this._tmp[14] = tmp;

        tmp = this._tmp[3];
        this._tmp[3] = this._tmp[7];
        this._tmp[7] = this._tmp[11];
        this._tmp[11] = this._tmp[15];
        this._tmp[15] = tmp;
    }

    /**
     * Changes all {@code byte}s of the state. This method is the inverse of
     * {@link Aes256#shiftRows}.
     */
    private void invSubstituteState() {
        for (int i = 0; i < BLOCK_SIZE; ++i) {
            this._tmp[i] = this._invSBox[this._tmp[i] & 0xff];
        }
    }

    /**
     * Mixes a column of the state. This method inverses {@link Aes256#mixColumn}.
     *
     * @param index position of the first entry of the row
     */
    private void invMixColumn(int index) {
        int s0 = mul(0x0e, this._tmp[index]) ^ mul(0x0b, this._tmp[index + 1])
                ^ mul(0x0d, this._tmp[index + 2]) ^ mul(0x09, this._tmp[index + 3]);
        int s1 = mul(0x09, this._tmp[index]) ^ mul(0x0e, this._tmp[index + 1])
                ^ mul(0x0b, this._tmp[index + 2]) ^ mul(0x0d, this._tmp[index + 3]);
        int s2 = mul(0x0d, this._tmp[index]) ^ mul(0x09, this._tmp[index + 1])
                ^ mul(0x0e, this._tmp[index + 2]) ^ mul(0x0b, this._tmp[index + 3]);
        int s3 = mul(0x0b, this._tmp[index]) ^ mul(0x0d, this._tmp[index + 1])
                ^ mul(0x09, this._tmp[index + 2]) ^ mul(0x0e, this._tmp[index + 3]);
        this._tmp[index] = (byte) (s0 & 0xff);
        this._tmp[index + 1] = (byte) (s1 & 0xff);
        this._tmp[index + 2] = (byte) (s2 & 0xff);
        this._tmp[index + 3] = (byte) (s3 & 0xff);
    }

    /**
     * Mixes all columns of the state. This method inverses {@link Aes256#mixColumns}.
     */
    private void invMixColumns() {
        invMixColumn(0);
        invMixColumn(4);
        invMixColumn(8);
        invMixColumn(12);
    }

    /**
     * Decrypts a block. The encrypted block starts at {@code inIndex} in {@code inBlock}.
     * {@code inBlock} won't be modified by this method. The decrypted block will be stored at
     * {@code outIndex} in {@code outBlock}.
     *
     * @param inBlock array containing the encrypted block
     * @param inIndex starting point of the encrypted block
     * @param outBlock array to store the decrypted block
     * @param outIndex position of the decrypted block
     */
    public void decrypt(byte[] inBlock, int inIndex, byte[] outBlock,
            int outIndex) {
        System.arraycopy(inBlock, inIndex, this._tmp, 0, BLOCK_SIZE);

        addRoundKey(ROUNDS * BLOCK_SIZE);
        for (int round = ROUNDS - 1; round > 0; --round) {
            invShiftRows();
            invSubstituteState();
            addRoundKey(round * BLOCK_SIZE);
            invMixColumns();
        }
        invShiftRows();
        invSubstituteState();
        addRoundKey(0);

        System.arraycopy(this._tmp, 0, outBlock, outIndex, BLOCK_SIZE);
    }
}
