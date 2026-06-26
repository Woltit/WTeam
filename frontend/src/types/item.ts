import { type PublicProfileResponse } from './user'

export type ItemCondition = 'IDEAL' | 'GOOD' | 'NORM' | 'BAD' | 'NEEDS_REPAIRING'
export type RentingStatus = 'AVAILABLE' | 'RENTED' | 'HIDDEN' | 'ARCHIVED'

export interface ItemRequest {
    categoryId: number
    title: string
    description: string | null
    tags: string[]
    condition: ItemCondition
    pricePerDay: number
    pricePerWeek: number | null
    depositAmount: number
    city: string
    address: string
    latitude: number
    longitude: number
}

export interface ItemResponse {
    id: number
    ownerId: number 
    ownerProfile: PublicProfileResponse
    categoryId: number
    title: string
    description: string | null
    tags: string[]
    condition: ItemCondition
    pricePerDay: number
    pricePerWeek: number | null
    depositAmount: number
    status: RentingStatus
    city: string
    address: string
    latitude: number
    longitude: number
    isVerified: boolean
    createdAt: string
    updatedAt: string
    images?: ItemImageResponse[]
}

export interface ItemImageResponse {
    id: number
    imageUrl: string
    isMain: boolean
}
