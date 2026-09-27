package FoodExpress.ui.login

import FoodExpress.dao.UsuarioDAO
import FoodExpress.theme.AppColors
import FoodExpress.utils.SessionInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import FoodExpress.utils.CacheManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import FoodExpress.model.Usuario
import kotlinx.coroutines.CoroutineScope

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var infoMessage by remember { mutableStateOf("") }

    var showRegisterDialog by remember { mutableStateOf(false) }
    var showRecoveryDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val dao = remember { UsuarioDAO() }

    LaunchedEffect(Unit) {
        // FIX SEC-003: Solo cargamos el username — password NUNCA se guarda en disco
        CacheManager.migrarArchivoLegacy()  // Elimina archivo viejo si existe
        val usuarioCacheado = CacheManager.cargarUsuario()
        if (usuarioCacheado != null) {
            username = usuarioCacheado
            rememberMe = true
            // password queda vacío — el usuario lo escribe siempre
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Surface),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.width(360.dp),
            elevation = 12.dp,
            shape = RoundedCornerShape(16.dp),
            backgroundColor = AppColors.CardBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🍔 FOODEXPRESS",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary
                )
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 14.sp,
                    color = AppColors.TextMuted,
                    modifier = Modifier.padding(bottom = 24.dp, top = 4.dp)
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = AppColors.Danger,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Usuario") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AppColors.Primary,
                        cursorColor = AppColors.Primary
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AppColors.Primary,
                        cursorColor = AppColors.Primary
                    )
                )

                // Fila para Recordar contraseña y Olvidé mi contraseña
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { rememberMe = !rememberMe }
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(checkedColor = AppColors.Primary)
                        )
                        Text(text = "Recordarme", fontSize = 12.sp, color = AppColors.TextMuted)
                    }

                    TextButton(onClick = { showRecoveryDialog = true }) {
                        Text("¿Olvidaste tu contraseña?", fontSize = 12.sp, color = AppColors.Primary)
                    }
                }

                if (infoMessage.isNotEmpty()) {
                    Text(
                        text = infoMessage,
                        color = AppColors.Primary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (username.isBlank() || password.isBlank()) {
                            errorMessage = "Por favor llena ambos campos."
                            return@Button
                        }
                        
                        isLoading = true
                        errorMessage = ""
                        infoMessage = ""
                        
                        scope.launch {
                            val result = withContext(Dispatchers.IO) {
                                dao.obtenerPorCredenciales(username, password)
                            }
                            
                            isLoading = false
                            result.onSuccess { sesion ->
                                if (sesion != null) {
                                    // FIX SEC-003: Solo guardar username, NUNCA la contraseña
                                    if (rememberMe) {
                                        CacheManager.guardarUsuario(username)
                                    } else {
                                        CacheManager.limpiarSesion()
                                    }
                                    // FIX SEC-007: sesion es UsuarioSesion (sin campo contrasena)
                                    SessionInfo.usuarioActual = sesion
                                    onLoginSuccess()
                                } else {
                                    errorMessage = "Credenciales incorrectas."
                                }
                            }.onFailure { error ->
                                errorMessage = "Error de conexión: ${error.message}"
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppColors.Primary,
                        contentColor = AppColors.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = AppColors.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Ingresar", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { showRegisterDialog = true }) {
                    Text("¿No tienes cuenta? Regístrate aquí", fontSize = 13.sp, color = AppColors.TextMuted)
                }
            }
        }
    }

    if (showRegisterDialog) {
        DialogoRegistro(
            dao = dao,
            scope = scope,
            onDismiss = { showRegisterDialog = false }
        )
    }

    if (showRecoveryDialog) {
        DialogoRecuperacion(
            dao = dao,
            scope = scope,
            onDismiss = { showRecoveryDialog = false }
        )
    }
}

@Composable
fun DialogoRegistro(dao: UsuarioDAO, scope: CoroutineScope, onDismiss: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var exitoRegistro by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (exitoRegistro) "¡Registro Exitoso!" else "Crear Nueva Cuenta", fontWeight = FontWeight.Bold) },
        text = {
            if (exitoRegistro) {
                Text(
                    text = "El usuario '$usuario' ha sido creado correctamente.\n\nYa puedes iniciar sesión en el sistema.",
                    color = AppColors.Success,
                    fontSize = 14.sp
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (error.isNotEmpty()) {
                        Text(error, color = AppColors.Danger, fontSize = 12.sp)
                    }
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre Completo") }, singleLine = true)
                    OutlinedTextField(value = usuario, onValueChange = { usuario = it }, label = { Text("Nombre de Usuario (Login)") }, singleLine = true)
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo Electrónico") }, singleLine = true)
                    OutlinedTextField(value = whatsapp, onValueChange = { whatsapp = it }, label = { Text("Número de WhatsApp") }, singleLine = true)
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            if (exitoRegistro) {
                Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.Success)) {
                    Text("Aceptar", color = AppColors.White)
                }
            } else {
                Button(
                    onClick = {
                        if (nombre.isBlank() || usuario.isBlank() || password.isBlank() || (email.isBlank() && whatsapp.isBlank())) {
                            error = "Llene los datos obligatorios. Se requiere Email o WhatsApp."
                            return@Button
                        }
                        loading = true
                        error = ""
                        scope.launch {
                            val nuevoUser = Usuario(
                                idUsuario = 0,
                                nombre = nombre,
                                usuario = usuario,
                                contrasena = password,
                                rol = "cajero", // Por defecto cajero
                                email = email,
                                whatsapp = whatsapp
                            )
                            val res = withContext(Dispatchers.IO) { dao.insertar(nuevoUser) }
                            loading = false
                            res.onSuccess { exito ->
                                if (exito) {
                                    exitoRegistro = true
                                } else {
                                    error = "No se pudo crear el usuario."
                                }
                            }.onFailure { e ->
                                error = e.message ?: "Error al registrar"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.Primary)
                ) {
                    if (loading) CircularProgressIndicator(Modifier.size(16.dp), color = AppColors.White, strokeWidth = 2.dp)
                    else Text("Registrarse", color = AppColors.White)
                }
            }
        },
        dismissButton = {
            if (!exitoRegistro) {
                TextButton(onClick = onDismiss) { Text("Cancelar", color = AppColors.TextMuted) }
            }
        }
    )
}

@Composable
fun DialogoRecuperacion(dao: UsuarioDAO, scope: CoroutineScope, onDismiss: () -> Unit) {
    var contacto by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var exitoMsg by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Recuperar Contraseña", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                if (exitoMsg.isNotEmpty()) {
                    Text(exitoMsg, color = AppColors.Success, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                } else {
                    Text("Ingresa tu Correo Electrónico o WhatsApp registrado. Se generará una clave temporal.", fontSize = 13.sp, color = AppColors.TextMuted)
                    Spacer(Modifier.height(12.dp))
                    if (error.isNotEmpty()) {
                        Text(error, color = AppColors.Danger, fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))
                    }
                    OutlinedTextField(
                        value = contacto,
                        onValueChange = { contacto = it },
                        label = { Text("Email o WhatsApp") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            if (exitoMsg.isEmpty()) {
                Button(
                    onClick = {
                        if (contacto.isBlank()) {
                            error = "Ingresa tu método de contacto"
                            return@Button
                        }
                        loading = true
                        error = ""
                        scope.launch {
                            val res = withContext(Dispatchers.IO) { dao.generarClaveTemporal(contacto) }
                            loading = false
                            res.onSuccess { clave ->
                                if (clave != null) {
                                    exitoMsg = "¡Clave generada! Por seguridad y al no tener servidor de mensajería configurado, tu clave temporal es: $clave\n\nPor favor, anótala, ingresa al sistema y avisa al administrador."
                                } else {
                                    error = "No se encontró ningún usuario con ese Email/WhatsApp."
                                }
                            }.onFailure { e ->
                                error = e.message ?: "Error al recuperar"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.Primary)
                ) {
                    if (loading) CircularProgressIndicator(Modifier.size(16.dp), color = AppColors.White, strokeWidth = 2.dp)
                    else Text("Recuperar", color = AppColors.White)
                }
            } else {
                Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.Success)) {
                    Text("Entendido", color = AppColors.White)
                }
            }
        },
        dismissButton = {
            if (exitoMsg.isEmpty()) {
                TextButton(onClick = onDismiss) { Text("Cancelar", color = AppColors.TextMuted) }
            }
        }
    )
}
