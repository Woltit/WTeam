export interface LoginRequest { 
    email: string
    password: string 
}

export interface RegisterRequest {
    email: string
    password: string
    checkPassword: string
}

export interface AuthResponse { 
    token: string
    refreshToken: string
}
