export type Role = 'ADMIN' | 'MODER' | 'USER'
export type VerificationStatus = 'UNVERIFIED' | 'PENDING' | 'VERIFIED' | 'REJECTED'

export interface UserProfileResponse {
    lastName: string
    firstName: string
    middleName: string | null
    birthDate: string | null
    phoneNumber: string | null
    bio: string | null
    verificationStatus: VerificationStatus
    avatarUrl: string | null
    renterTrustScore: number | null
    ownerTrustScore: number | null
    totalSuccessfulRents: number
}

export interface UserResponse {
  id: number
  email: string
  role: Role
  isActive: boolean
  blockReason: string | null
  profile: UserProfileResponse | null
  createdAt: string
}

export interface PendingProfileResponse {
  userId: number
  email: string
  lastName: string
  firstName: string
  middleName: string | null
  birthDate: string | null
  phoneNumber: string | null
  verificationStatus: VerificationStatus
}

export interface BlockUserRequest {
    reason: string
}

export interface PublicProfileResponse {
    lastName: string
    firstName: string
    middleName: string | null
    bio: string | null
    avatarUrl: string | null
    renterTrustScore: number | null
    ownerTrustScore: number | null
    totalSuccessfulRents: number | null
}
