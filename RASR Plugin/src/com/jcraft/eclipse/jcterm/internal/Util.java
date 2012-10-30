/*******************************************************************************
 * Copyright (c) 2007 JCraft, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     JCraft, Inc. - initial API and implementation
 *******************************************************************************/

package com.jcraft.eclipse.jcterm.internal;

public class Util {
	private static final byte[] b64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="
			.getBytes();

	private static byte val(byte foo) {
		if (foo == '=')
			return 0;
		for (int j = 0; j < b64.length; j++) {
			if (foo == b64[j])
				return (byte) j;
		}
		return 0;
	}

	public static byte[] fromBase64(byte[] buf, int start, int length) {
		byte[] foo = new byte[length];
		int j = 0;
		for (int i = start; i < start + length; i += 4) {
			foo[j] = (byte) ((val(buf[i]) << 2) | ((val(buf[i + 1]) & 0x30) >>> 4));
			if (buf[i + 2] == (byte) '=') {
				j++;
				break;
			}
			foo[j + 1] = (byte) (((val(buf[i + 1]) & 0x0f) << 4) | ((val(buf[i + 2]) & 0x3c) >>> 2));
			if (buf[i + 3] == (byte) '=') {
				j += 2;
				break;
			}
			foo[j + 2] = (byte) (((val(buf[i + 2]) & 0x03) << 6) | (val(buf[i + 3]) & 0x3f));
			j += 3;
		}
		byte[] bar = new byte[j];
		System.arraycopy(foo, 0, bar, 0, j);
		return bar;
	}

	public static byte[] toBase64(byte[] buf, int start, int length) {

		byte[] tmp = new byte[length * 2];
		int i, j, k;

		int foo = (length / 3) * 3 + start;
		i = 0;
		for (j = start; j < foo; j += 3) {
			k = (buf[j] >>> 2) & 0x3f;
			tmp[i++] = b64[k];
			k = (buf[j] & 0x03) << 4 | (buf[j + 1] >>> 4) & 0x0f;
			tmp[i++] = b64[k];
			k = (buf[j + 1] & 0x0f) << 2 | (buf[j + 2] >>> 6) & 0x03;
			tmp[i++] = b64[k];
			k = buf[j + 2] & 0x3f;
			tmp[i++] = b64[k];
		}

		foo = (start + length) - foo;
		if (foo == 1) {
			k = (buf[j] >>> 2) & 0x3f;
			tmp[i++] = b64[k];
			k = ((buf[j] & 0x03) << 4) & 0x3f;
			tmp[i++] = b64[k];
			tmp[i++] = (byte) '=';
			tmp[i++] = (byte) '=';
		} else if (foo == 2) {
			k = (buf[j] >>> 2) & 0x3f;
			tmp[i++] = b64[k];
			k = (buf[j] & 0x03) << 4 | (buf[j + 1] >>> 4) & 0x0f;
			tmp[i++] = b64[k];
			k = ((buf[j + 1] & 0x0f) << 2) & 0x3f;
			tmp[i++] = b64[k];
			tmp[i++] = (byte) '=';
		}
		byte[] bar = new byte[i];
		System.arraycopy(tmp, 0, bar, 0, i);
		return bar;
	}
}
