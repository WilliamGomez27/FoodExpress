package FastFoodApp.ui.login

import FastFoodApp.dao.UsuarioDAO
import FastFoodApp.theme.AppColors
import FastFoodApp.utils.SessionInfo
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
import FastFoodApp.utils.CacheManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var infoMessage by remember { mutableStateOf("") }

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
                    text = "🍔 RAPPYFOOD",
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

                    TextButton(onClick = { 
                        infoMessage = "Contacta al administrador (admin) para recuperar credenciales."
                    }) {
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
                                errorMessage = "Error de conexión. Verifica la red."
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
            }
        }
    }
}
