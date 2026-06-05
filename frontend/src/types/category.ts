export interface CategoryRequest {
    name: string
    slug: string
    iconUrl: string | null
    parentId: number | null
}

export interface CategoryResponse {
    id: number
    name: string
    slug: string
    iconUrl: string | null
    subcategories: CategoryResponse[]
}
