package org.bonitasoft.gdrive.core

interface Logger {
	fun debug(message: String): () -> Unit = {}
	fun info(message: String): () -> Unit = {}
	fun warn(message: String): () -> Unit = {}
	fun error(message: String): () -> Unit = {}
}
